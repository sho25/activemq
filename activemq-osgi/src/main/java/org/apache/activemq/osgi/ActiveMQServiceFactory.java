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
name|osgi
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
name|activemq
operator|.
name|spring
operator|.
name|Utils
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
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|cm
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|cm
operator|.
name|ManagedServiceFactory
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
name|ConfigurableApplicationContext
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
name|support
operator|.
name|ClassPathXmlApplicationContext
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
name|support
operator|.
name|PropertySourcesPlaceholderConfigurer
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveMQServiceFactory
implements|implements
name|ManagedServiceFactory
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveMQServiceFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|BundleContext
name|bundleContext
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|BrokerService
argument_list|>
name|brokers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"ActiveMQ Server Controller"
return|;
block|}
annotation|@
name|Override
specifier|synchronized
specifier|public
name|void
name|updated
parameter_list|(
name|String
name|pid
parameter_list|,
name|Dictionary
name|properties
parameter_list|)
throws|throws
name|ConfigurationException
block|{
comment|// First stop currently running broker (if any)
name|deleted
argument_list|(
name|pid
argument_list|)
expr_stmt|;
name|String
name|config
init|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"config"
argument_list|,
literal|"Property must be set"
argument_list|)
throw|;
block|}
name|String
name|name
init|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"broker-name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"broker-name"
argument_list|,
literal|"Property must be set"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker "
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|BrokerService
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|Utils
operator|.
name|resourceFromString
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|ResourceXmlApplicationContext
name|ctx
init|=
operator|new
name|ResourceXmlApplicationContext
argument_list|(
name|resource
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
literal|false
argument_list|)
block|{
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
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|// Handle properties in configuration
name|PropertySourcesPlaceholderConfigurer
name|configurator
init|=
operator|new
name|PropertySourcesPlaceholderConfigurer
argument_list|()
decl_stmt|;
comment|//convert dictionary to properties. Is there a better way?
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Enumeration
name|elements
init|=
name|properties
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|elements
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Object
name|key
init|=
name|elements
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|properties
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|configurator
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|configurator
operator|.
name|setIgnoreUnresolvablePlaceholders
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addBeanFactoryPostProcessor
argument_list|(
name|configurator
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// Start the broker
name|BrokerService
name|broker
init|=
name|ctx
operator|.
name|getBean
argument_list|(
name|BrokerService
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|null
argument_list|,
literal|"Broker not defined"
argument_list|)
throw|;
block|}
comment|//TODO deal with multiple brokers
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|pid
argument_list|,
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|null
argument_list|,
literal|"Cannot start the broker"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|synchronized
specifier|public
name|void
name|deleted
parameter_list|(
name|String
name|pid
parameter_list|)
block|{
name|BrokerService
name|broker
init|=
name|brokers
operator|.
name|get
argument_list|(
name|pid
argument_list|)
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker "
operator|+
name|pid
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception on stopping broker"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|synchronized
specifier|public
name|void
name|destroy
parameter_list|()
block|{
for|for
control|(
name|String
name|broker
range|:
name|brokers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|deleted
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|BundleContext
name|getBundleContext
parameter_list|()
block|{
return|return
name|bundleContext
return|;
block|}
specifier|public
name|void
name|setBundleContext
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
block|{
name|this
operator|.
name|bundleContext
operator|=
name|bundleContext
expr_stmt|;
block|}
block|}
end_class

end_unit
