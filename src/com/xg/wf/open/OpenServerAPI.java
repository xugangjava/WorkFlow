package com.xg.wf.open;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/open")
public class OpenServerAPI {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	RuntimeService runtimeService;
	
	
	@Autowired
	IdentityService identityService;
	
	@Autowired
	TaskService taskService;


	@RequestMapping(value = "list")
	public @ResponseBody 
	List<Model> modelList() {
		List<Model> list = repositoryService.createModelQuery().list();
		return list;
	}
	
	@RequestMapping(value = "create")
	public void create(@RequestParam("name") String name,
			@RequestParam("key") String key,
			@RequestParam("description") String description,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace",
					"http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			Model modelData = repositoryService.newModel();

			ObjectNode modelObjectNode = objectMapper.createObjectNode();
			modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			description = StringUtils.defaultString(description);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,
					description);
			modelData.setMetaInfo(modelObjectNode.toString());
			modelData.setName(name);
			modelData.setKey(StringUtils.defaultString(key));

			repositoryService.saveModel(modelData);
			repositoryService.addModelEditorSource(modelData.getId(),
					editorNode.toString().getBytes("utf-8"));
			response.sendRedirect(request.getContextPath()+ "/api/editor?id=" + modelData.getId());
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}


	@RequestMapping(value = "deploy")
	public @ResponseBody JSONObject deploy(@RequestParam("modelId") String modelId) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			ObjectNode modelNode = (ObjectNode) new ObjectMapper()
					.readTree(repositoryService.getModelEditorSource(modelData
							.getId()));
			byte[] bpmnBytes = null;
			BpmnModel model = new BpmnJsonConverter()
					.convertToBpmnModel(modelNode);
			bpmnBytes = new BpmnXMLConverter().convertToXML(model);
			String processName = modelData.getName() + ".bpmn20.xml";
			repositoryService.createDeployment()
					.name(modelData.getName())
					.addString(processName, new String(bpmnBytes)).deploy();
		
		} catch (Exception e) {
			logger.error("部署失败 modelId={}", modelId, e);
			return errCallback("部署失败！");
		}
		return callback("部署成功！");
	}


	@RequestMapping(value = "export")
	public void export(@RequestParam("modelId") String modelId,
			HttpServletResponse response) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			JsonNode editorNode = new ObjectMapper().readTree(repositoryService
					.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

			ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
			IOUtils.copy(in, response.getOutputStream());
			String filename = bpmnModel.getMainProcess().getId()
					+ ".bpmn20.xml";
			response.setHeader("Content-Disposition", "attachment; filename="
					+ filename);
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("导出model失败modelId={}", modelId, e);
		}
	}
	
	
	@RequestMapping(value = "return")
	public @ResponseBody
	JSONObject getReturn() {
		JSONObject result=new JSONObject();
			
		return result;
	}
	
	

	@RequestMapping(value = "check")
	public @ResponseBody
	JSONObject check() {
		JSONObject result=new JSONObject();
		
		return result;
	}
	

	@RequestMapping(value = "backcheck")
	public @ResponseBody
	JSONObject backCheck() {
		JSONObject result=new JSONObject();
		
		return result;
	}
	
	

	@RequestMapping(value = "start")
	public @ResponseBody JSONObject start(String userId,String key,
			String businesskey, 
			Map<String, Object> map) {
		identityService.setAuthenticatedUserId(userId);
		ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(key, businesskey, map);
		
		JSONObject obj=new JSONObject();
		obj.put("processId", processInstance.getProcessDefinitionId());
		return obj;
		
	}
	
	

	@RequestMapping(value = "finish")
	public @ResponseBody JSONObject finish(String userId,String key,
			String businesskey, 
			Map<String, Object> map) {
		identityService.setAuthenticatedUserId(userId);
		ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(key, businesskey, map);

		JSONObject obj=new JSONObject();
		obj.put("processId", processInstance.getProcessDefinitionId());
		return obj;
	}
	
	
	
	
	public JSONObject callback(String msg){
		JSONObject obj=new JSONObject();
		obj.put("success", true);
		obj.put("message", msg);
		return obj;
	}
	
	public JSONObject errCallback(String msg){
		JSONObject obj=new JSONObject();
		obj.put("success", false);
		obj.put("message", msg);
		return obj;
	}

}
