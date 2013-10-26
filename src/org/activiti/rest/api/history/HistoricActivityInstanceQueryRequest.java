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

package org.activiti.rest.api.history;



/**
 * @author Tijs Rademakers
 */
public class HistoricActivityInstanceQueryRequest {

  private String activityId;
  private String activityInstanceId;
  private String activityName;
  private String activityType;
  private String executionId;
  private Boolean finished;
  private String taskAssignee;
  private String processInstanceId;
  private String processDefinitionId;
  
  public String getActivityId() {
    return activityId;
  }
  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }
  public String getActivityInstanceId() {
    return activityInstanceId;
  }
  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
  }
  public String getActivityName() {
    return activityName;
  }
  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }
  public String getActivityType() {
    return activityType;
  }
  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }
  public String getExecutionId() {
    return executionId;
  }
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }
  public Boolean getFinished() {
    return finished;
  }
  public void setFinished(Boolean finished) {
    this.finished = finished;
  }
  public String getTaskAssignee() {
    return taskAssignee;
  }
  public void setTaskAssignee(String taskAssignee) {
    this.taskAssignee = taskAssignee;
  }
  public String getProcessInstanceId() {
    return processInstanceId;
  }
  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }
  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }
}
