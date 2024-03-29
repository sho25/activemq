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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|PostConstruct
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|PreDestroy
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
name|BrokerFactory
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
name|usage
operator|.
name|SystemUsage
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
name|CachedIntrospectionResults
import|;
end_import

begin_comment
comment|/**  * An ActiveMQ Message Broker. It consists of a number of transport  * connectors, network connectors and a bunch of properties which can be used to  * configure the broker as its lazily created.  *  * @org.apache.xbean.XBean element="broker" rootElement="true"  * @org.apache.xbean.Defaults {code:xml}  *<broker test="foo.bar">  *   lets.  *   see what it includes.  *</broker>  * {code}  *  */
end_comment

begin_class
specifier|public
class|class
name|XBeanBrokerService
extends|extends
name|BrokerService
block|{
specifier|private
name|boolean
name|start
decl_stmt|;
specifier|public
name|XBeanBrokerService
parameter_list|()
block|{
name|start
operator|=
name|BrokerFactory
operator|.
name|getStartDefault
argument_list|()
expr_stmt|;
block|}
comment|/**      * JSR-250 callback wrapper; converts checked exceptions to runtime exceptions      *      * delegates to afterPropertiesSet, done to prevent backwards incompatible signature change.      */
annotation|@
name|PostConstruct
specifier|private
name|void
name|postConstruct
parameter_list|()
block|{
try|try
block|{
name|afterPropertiesSet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      *      * @throws Exception      * @org.apache.xbean.InitMethod      */
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
name|ensureSystemUsageHasStore
argument_list|()
expr_stmt|;
if|if
condition|(
name|shouldAutostart
argument_list|()
condition|)
block|{
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|shouldAutostart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
specifier|private
name|void
name|ensureSystemUsageHasStore
parameter_list|()
throws|throws
name|IOException
block|{
name|SystemUsage
name|usage
init|=
name|getSystemUsage
argument_list|()
decl_stmt|;
if|if
condition|(
name|usage
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|usage
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setStore
argument_list|(
name|getPersistenceAdapter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setStore
argument_list|(
name|getTempDataStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|usage
operator|.
name|getJobSchedulerUsage
argument_list|()
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|usage
operator|.
name|getJobSchedulerUsage
argument_list|()
operator|.
name|setStore
argument_list|(
name|getJobSchedulerStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * JSR-250 callback wrapper; converts checked exceptions to runtime exceptions      *      * delegates to destroy, done to prevent backwards incompatible signature change.      */
annotation|@
name|PreDestroy
specifier|private
name|void
name|preDestroy
parameter_list|()
block|{
try|try
block|{
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      *      * @throws Exception      * @org.apache.xbean.DestroyMethod      */
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
comment|// must clear this Spring cache to avoid any memory leaks
name|CachedIntrospectionResults
operator|.
name|clearClassLoader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets whether or not the broker is started along with the ApplicationContext it is defined within.      * Normally you would want the broker to start up along with the ApplicationContext but sometimes when working      * with JUnit tests you may wish to start and stop the broker explicitly yourself.      */
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

