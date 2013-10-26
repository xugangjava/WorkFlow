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

package org.activiti.rest.api.repository;

import org.activiti.engine.repository.Model;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.application.ActivitiRestServicesApplication;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;


/**
 * @author Frederik Heremans
 */
public class ModelResource extends BaseModelResource {

  @Get
  public ModelResponse getModel() {
    Model model = getModelFromRequest();
    
    return getApplication(ActivitiRestServicesApplication.class).getRestResponseFactory()
            .createModelResponse(this, model);
  }
  
  @Put
  public ModelResponse updateModel(ModelRequest request) {
    
    Model model = getModelFromRequest();
    
    if(request.isCategoryChanged()) {
      model.setCategory(request.getCategory());
    }
    if(request.isDeploymentChanged()) {
      model.setDeploymentId(request.getDeploymentId());
    }
    if(request.isKeyChanged()) {
      model.setKey(request.getKey());
    }
    if(request.isMetaInfoChanged()) {
      model.setMetaInfo(request.getMetaInfo());
    }
    if(request.isNameChanged()) {
      model.setName(request.getName());
    }
    if(request.isVersionChanged()) {
      model.setVersion(request.getVersion());
    }
    
    ActivitiUtil.getRepositoryService().saveModel(model);
    return getApplication(ActivitiRestServicesApplication.class).getRestResponseFactory()
            .createModelResponse(this, model);
  }

  @Delete
  public void deleteModel() {
    Model model = getModelFromRequest();
    ActivitiUtil.getRepositoryService().deleteModel(model.getId());
    setStatus(Status.SUCCESS_NO_CONTENT);
  }
}
