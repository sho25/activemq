begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DisposableBean
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
name|FactoryBean
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
name|InitializingBean
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
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * A Spring {@link FactoryBean} which creates an embedded broker inside a Spring  * XML using an external<a href="http://gbean.org/Custom+XML">XBean Spring XML  * configuration file</a> which provides a much neater and more concise XML  * format.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|BrokerFactoryBean
implements|implements
name|FactoryBean
implements|,
name|InitializingBean
implements|,
name|DisposableBean
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BrokerFactoryBean
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
name|Resource
name|config
decl_stmt|;
specifier|private
name|XBeanBrokerService
name|broker
decl_stmt|;
specifier|private
name|boolean
name|start
init|=
literal|false
decl_stmt|;
specifier|private
name|ResourceXmlApplicationContext
name|context
decl_stmt|;
specifier|public
name|BrokerFactoryBean
parameter_list|()
block|{     }
specifier|public
name|BrokerFactoryBean
parameter_list|(
name|Resource
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|broker
return|;
block|}
specifier|public
name|Class
name|getObjectType
parameter_list|()
block|{
return|return
name|BrokerService
operator|.
name|class
return|;
block|}
specifier|public
name|boolean
name|isSingleton
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"config property must be set"
argument_list|)
throw|;
block|}
name|context
operator|=
operator|new
name|ResourceXmlApplicationContext
argument_list|(
name|config
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|=
operator|(
name|XBeanBrokerService
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
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"No bean named broker available: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|XBeanBrokerService
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
name|start
condition|)
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Resource
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
specifier|public
name|void
name|setConfig
parameter_list|(
name|Resource
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|getBroker
parameter_list|()
block|{
return|return
name|broker
return|;
block|}
specifier|public
name|boolean
name|isStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
specifier|public
name|void
name|setStart
parameter_list|(
name|boolean
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
block|}
end_class

end_unit

