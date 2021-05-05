package ${cfg.feignPackage};

import ${package.Entity}.${entity};
<#--如果有核心feign 则进行导入 否则不导入-->
<#if cfg.coreFeignPath??>
import ${cfg.coreFeignPath};
</#if>
import org.springframework.cloud.openfeign.FeignClient;

/**
* @author ljh
* @version 1.0
* @date 2021/4/6 15:49
* @description 标题
* @package
*/
@FeignClient(name="${cfg.feignApplicationName}",path = "/${table.entityPath}",contextId = "${table.entityPath}")
public interface ${entity}Feign <#if cfg.coreFeignPath??> extends  ${cfg.coreFeignClassName}<${entity}> </#if>{

}