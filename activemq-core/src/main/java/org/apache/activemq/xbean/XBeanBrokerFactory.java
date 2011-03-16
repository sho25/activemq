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
name|xbean
package|;
end_package

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyEditorManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerFactoryHandler
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
name|BrokerService
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
name|util
operator|.
name|IntrospectionSupport
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
name|util
operator|.
name|URISupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xbean
operator|.
name|spring
operator|.
name|context
operator|.
name|ResourceXmlApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xbean
operator|.
name|spring
operator|.
name|context
operator|.
name|impl
operator|.
name|URIEditor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|BeansException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|xml
operator|.
name|XmlBeanDefinitionReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|ApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|ApplicationContextAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|FileSystemResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|UrlResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|util
operator|.
name|ResourceUtils
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|XBeanBrokerFactory
implements|implements
name|BrokerFactoryHandler
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XBeanBrokerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|PropertyEditorManager
operator|.
name|registerEditor
argument_list|(
name|URI
operator|.
name|class
argument_list|,
name|URIEditor
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|validate
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|isValidate
parameter_list|()
block|{
return|return
name|validate
return|;
block|}
specifier|public
name|void
name|setValidate
parameter_list|(
name|boolean
name|validate
parameter_list|)
block|{
name|this
operator|.
name|validate
operator|=
name|validate
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|URI
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
name|config
operator|.
name|getSchemeSpecificPart
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
name|URISupport
operator|.
name|parseQuery
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parameters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|uri
operator|.
name|lastIndexOf
argument_list|(
literal|'?'
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ApplicationContext
name|context
init|=
name|createApplicationContext
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"broker"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BeansException
name|e
parameter_list|)
block|{         }
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
comment|// lets try find by type
name|String
index|[]
name|names
init|=
name|context
operator|.
name|getBeanNamesForType
argument_list|(
name|BrokerService
operator|.
name|class
argument_list|)
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|names
index|[
name|i
index|]
decl_stmt|;
name|broker
operator|=
operator|(
name|BrokerService
operator|)
name|context
operator|.
name|getBean
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The configuration has no BrokerService instance for resource: "
operator|+
name|config
argument_list|)
throw|;
block|}
if|if
condition|(
name|broker
operator|instanceof
name|ApplicationContextAware
condition|)
block|{
operator|(
operator|(
name|ApplicationContextAware
operator|)
name|broker
operator|)
operator|.
name|setApplicationContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|// TODO warning resources from the context may not be closed down!
return|return
name|broker
return|;
block|}
specifier|protected
name|ApplicationContext
name|createApplicationContext
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Now attempting to figure out the type of resource: "
operator|+
name|uri
argument_list|)
expr_stmt|;
name|Resource
name|resource
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|resource
operator|=
operator|new
name|FileSystemResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ResourceUtils
operator|.
name|isUrl
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|resource
operator|=
operator|new
name|UrlResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resource
operator|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ResourceXmlApplicationContext
argument_list|(
name|resource
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|initBeanDefinitionReader
parameter_list|(
name|XmlBeanDefinitionReader
name|reader
parameter_list|)
block|{
name|reader
operator|.
name|setValidating
argument_list|(
name|isValidate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

