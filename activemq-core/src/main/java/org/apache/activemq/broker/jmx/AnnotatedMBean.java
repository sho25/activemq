begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|util
operator|.
name|AuditLog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|util
operator|.
name|AuditLogService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|util
operator|.
name|DefaultAuditLog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * MBean that looks for method/parameter descriptions in the Info annotation.  */
end_comment

begin_class
specifier|public
class|class
name|AnnotatedMBean
extends|extends
name|StandardMBean
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|primitives
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.activemq.audit"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|audit
decl_stmt|;
specifier|private
specifier|static
name|AuditLogService
name|auditLog
decl_stmt|;
static|static
block|{
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|p
init|=
block|{
name|byte
operator|.
name|class
block|,
name|short
operator|.
name|class
block|,
name|int
operator|.
name|class
block|,
name|long
operator|.
name|class
block|,
name|float
operator|.
name|class
block|,
name|double
operator|.
name|class
block|,
name|char
operator|.
name|class
block|,
name|boolean
operator|.
name|class
block|, }
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
range|:
name|p
control|)
block|{
name|primitives
operator|.
name|put
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|audit
operator|=
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activemq.audit"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|audit
condition|)
block|{
name|auditLog
operator|=
name|AuditLogService
operator|.
name|getAuditLog
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
name|void
name|registerMBean
parameter_list|(
name|ManagementContext
name|context
parameter_list|,
name|Object
name|object
parameter_list|,
name|ObjectName
name|objectName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|mbeanName
init|=
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"MBean"
decl_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
if|if
condition|(
name|mbeanName
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|context
operator|.
name|registerMBean
argument_list|(
operator|new
name|AnnotatedMBean
argument_list|(
name|object
argument_list|,
name|c
argument_list|)
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|context
operator|.
name|registerMBean
argument_list|(
name|object
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
block|}
comment|/** Instance where the MBean interface is implemented by another object. */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|AnnotatedMBean
parameter_list|(
name|T
name|impl
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|mbeanInterface
parameter_list|)
throws|throws
name|NotCompliantMBeanException
block|{
name|super
argument_list|(
name|impl
argument_list|,
name|mbeanInterface
argument_list|)
expr_stmt|;
block|}
comment|/** Instance where the MBean interface is implemented by this object. */
specifier|protected
name|AnnotatedMBean
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|mbeanInterface
parameter_list|)
throws|throws
name|NotCompliantMBeanException
block|{
name|super
argument_list|(
name|mbeanInterface
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
specifier|protected
name|String
name|getDescription
parameter_list|(
name|MBeanAttributeInfo
name|info
parameter_list|)
block|{
name|String
name|descr
init|=
name|info
operator|.
name|getDescription
argument_list|()
decl_stmt|;
name|Method
name|m
init|=
name|getMethod
argument_list|(
name|getMBeanInterface
argument_list|()
argument_list|,
literal|"get"
operator|+
name|info
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
operator|+
name|info
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|m
operator|=
name|getMethod
argument_list|(
name|getMBeanInterface
argument_list|()
argument_list|,
literal|"is"
operator|+
name|info
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
operator|+
name|info
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|m
operator|=
name|getMethod
argument_list|(
name|getMBeanInterface
argument_list|()
argument_list|,
literal|"does"
operator|+
name|info
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
operator|+
name|info
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|MBeanInfo
name|d
init|=
name|m
operator|.
name|getAnnotation
argument_list|(
name|MBeanInfo
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
name|descr
operator|=
name|d
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
return|return
name|descr
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
specifier|protected
name|String
name|getDescription
parameter_list|(
name|MBeanOperationInfo
name|op
parameter_list|)
block|{
name|String
name|descr
init|=
name|op
operator|.
name|getDescription
argument_list|()
decl_stmt|;
name|Method
name|m
init|=
name|getMethod
argument_list|(
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|MBeanInfo
name|d
init|=
name|m
operator|.
name|getAnnotation
argument_list|(
name|MBeanInfo
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
name|descr
operator|=
name|d
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
return|return
name|descr
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
specifier|protected
name|String
name|getParameterName
parameter_list|(
name|MBeanOperationInfo
name|op
parameter_list|,
name|MBeanParameterInfo
name|param
parameter_list|,
name|int
name|paramNo
parameter_list|)
block|{
name|String
name|name
init|=
name|param
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Method
name|m
init|=
name|getMethod
argument_list|(
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Annotation
name|a
range|:
name|m
operator|.
name|getParameterAnnotations
argument_list|()
index|[
name|paramNo
index|]
control|)
block|{
if|if
condition|(
name|MBeanInfo
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|a
argument_list|)
condition|)
name|name
operator|=
name|MBeanInfo
operator|.
name|class
operator|.
name|cast
argument_list|(
name|a
argument_list|)
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|name
return|;
block|}
comment|/**    * Extracts the Method from the MBeanOperationInfo    * @param op    * @return    */
specifier|private
name|Method
name|getMethod
parameter_list|(
name|MBeanOperationInfo
name|op
parameter_list|)
block|{
specifier|final
name|MBeanParameterInfo
index|[]
name|params
init|=
name|op
operator|.
name|getSignature
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|paramTypes
init|=
operator|new
name|String
index|[
name|params
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|paramTypes
index|[
name|i
index|]
operator|=
name|params
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
expr_stmt|;
return|return
name|getMethod
argument_list|(
name|getMBeanInterface
argument_list|()
argument_list|,
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|paramTypes
argument_list|)
return|;
block|}
comment|/**    * Returns the Method with the specified name and parameter types for the given class,    * null if it doesn't exist.    * @param mbean    * @param method    * @param params    * @return    */
specifier|private
specifier|static
name|Method
name|getMethod
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|mbean
parameter_list|,
name|String
name|method
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ClassLoader
name|loader
init|=
name|mbean
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|paramClasses
init|=
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[
name|params
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|paramClasses
index|[
name|i
index|]
operator|=
name|primitives
operator|.
name|get
argument_list|(
name|params
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|paramClasses
index|[
name|i
index|]
operator|==
literal|null
condition|)
name|paramClasses
index|[
name|i
index|]
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
return|return
name|mbean
operator|.
name|getMethod
argument_list|(
name|method
argument_list|,
name|paramClasses
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|,
name|String
index|[]
name|strings
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
if|if
condition|(
name|audit
condition|)
block|{
name|Subject
name|subject
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|caller
init|=
literal|"anonymous"
decl_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|caller
operator|=
literal|""
expr_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|subject
operator|.
name|getPrincipals
argument_list|()
control|)
block|{
name|caller
operator|+=
name|principal
operator|.
name|getName
argument_list|()
operator|+
literal|" "
expr_stmt|;
block|}
block|}
name|auditLog
operator|.
name|log
argument_list|(
name|caller
operator|.
name|trim
argument_list|()
operator|+
literal|" called "
operator|+
name|this
operator|.
name|getMBeanInfo
argument_list|()
operator|.
name|getClassName
argument_list|()
operator|+
literal|"."
operator|+
name|s
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|objects
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|invoke
argument_list|(
name|s
argument_list|,
name|objects
argument_list|,
name|strings
argument_list|)
return|;
block|}
block|}
end_class

end_unit

