/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.explorer.ui.login;
import java.security.MessageDigest;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.explorer.Constants;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.identity.LoggedInUserImpl;

/**
 * Default login handler, using activiti's {@link IdentityService}.
 * 
 * @author Frederik Heremans
 */
public class DefaultLoginHandler implements LoginHandler {

	private static final long serialVersionUID = 3020533782422378779L;
	
	private transient IdentityService identityService;

	private String getMD5(String pwd) {
		try {
	        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
	                'a', 'b', 'c', 'd', 'e', 'f' };  
            byte[] strTemp = "11111aaaaa@".getBytes();  
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");  
            mdTemp.update(strTemp);  
            byte[] md = mdTemp.digest();  
            int j = md.length;  
            char str[] = new char[j * 2];  
            int k = 0;  
            for (int i = 0; i < j; i++) {  
                byte byte0 = md[i];  
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
                str[k++] = hexDigits[byte0 & 0xf];  
            }
           return new String(str).toUpperCase();  
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	public LoggedInUserImpl authenticate(String userName, String password) {
		LoggedInUserImpl loggedInUser = null;
		password=getMD5(password);
		if (identityService.checkPassword(userName, password)) {
			User user = identityService.createUserQuery().userId(userName)
					.singleResult();
			// Fetch and cache user data
			loggedInUser = new LoggedInUserImpl(user, password);
			List<Group> groups = identityService.createGroupQuery()
					.groupMember(user.getId()).list();
			for (Group group : groups) {

				if (Constants.SECURITY_ROLE.equals(group.getType())) {
					loggedInUser.addSecurityRoleGroup(group);
					if (Constants.SECURITY_ROLE_USER.equals(group.getId())) {
						loggedInUser.setUser(true);
					}
					if (Constants.SECURITY_ROLE_ADMIN.equals(group.getId())) {
						loggedInUser.setAdmin(true);
					}
				} else if (ExplorerApp.get().getAdminGroups() != null
						&& ExplorerApp.get().getAdminGroups()
								.contains(group.getId())) {
					loggedInUser.addSecurityRoleGroup(group);
					loggedInUser.setAdmin(true);
				} else if (ExplorerApp.get().getUserGroups() != null
						&& ExplorerApp.get().getUserGroups()
								.contains(group.getId())) {
					loggedInUser.addSecurityRoleGroup(group);
					loggedInUser.setUser(true);
				} else {
					loggedInUser.addGroup(group);
				}

			}
		}

		return loggedInUser;
	}

	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		// Noting to do here
	}

	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		// Noting to do here
	}

	public LoggedInUser authenticate(HttpServletRequest request,
			HttpServletResponse response) {
		// No automatic authentication is used by default, always through
		// credentials.
		return null;
	}

	public void logout(LoggedInUser userToLogout) {
		// Clear activiti authentication context
		Authentication.setAuthenticatedUserId(null);
	}

	public void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}

}
