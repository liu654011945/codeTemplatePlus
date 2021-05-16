package ${package.Controller};


import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
<#if swagger2>
import io.swagger.annotations.Api;
</#if>
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

/**
* <p>
* ${table.comment!} 控制器</p>
* @author ${author}
* @since ${date}
*/
<#if swagger2>
@Api(value="${table.comment!}",tags = "${table.controllerName}")
</#if>
@RestController
@RequestMapping("/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass}<${entity}> {

    private ${table.serviceName} ${table.serviceName?uncap_first};

    //注入
    @Autowired
    public ${table.controllerName}(${table.serviceName} ${table.serviceName?uncap_first}) {
        super(${table.serviceName?uncap_first});
        this.${table.serviceName?uncap_first}=${table.serviceName?uncap_first};
    }
<#else>
    public class ${table.controllerName} {
</#if>

}

