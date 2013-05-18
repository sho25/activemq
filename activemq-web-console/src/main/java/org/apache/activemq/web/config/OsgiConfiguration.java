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
name|web
operator|.
name|config
package|;
end_package

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
name|framework
operator|.
name|Constants
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
name|FrameworkUtil
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
name|ServiceRegistration
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
name|ManagedService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_class
specifier|public
class|class
name|OsgiConfiguration
extends|extends
name|AbstractConfiguration
implements|implements
name|ManagedService
block|{
specifier|private
name|ServiceRegistration
name|service
decl_stmt|;
specifier|private
name|String
name|jmxUrl
init|=
literal|"service:jmx:rmi:///jndi/rmi://localhost:1099/karaf-root"
decl_stmt|;
specifier|private
name|String
name|jmxUser
init|=
literal|"karaf"
decl_stmt|;
specifier|private
name|String
name|jmxPassword
init|=
literal|"karaf"
decl_stmt|;
specifier|private
name|String
name|jmsUrl
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
name|String
name|jmsUser
init|=
literal|"karaf"
decl_stmt|;
specifier|private
name|String
name|jmsPassword
init|=
literal|"karaf"
decl_stmt|;
specifier|public
name|OsgiConfiguration
parameter_list|()
block|{
name|BundleContext
name|context
init|=
name|FrameworkUtil
operator|.
name|getBundle
argument_list|(
name|getClass
argument_list|()
argument_list|)
operator|.
name|getBundleContext
argument_list|()
decl_stmt|;
name|Dictionary
name|properties
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Constants
operator|.
name|SERVICE_PID
argument_list|,
literal|"org.apache.activemq.webconsole"
argument_list|)
expr_stmt|;
name|service
operator|=
name|context
operator|.
name|registerService
argument_list|(
name|ManagedService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJmxPassword
parameter_list|()
block|{
return|return
name|jmxPassword
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|JMXServiceURL
argument_list|>
name|getJmxUrls
parameter_list|()
block|{
return|return
name|makeJmxUrls
argument_list|(
name|jmxUrl
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJmxUser
parameter_list|()
block|{
return|return
name|jmxUser
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|makeConnectionFactory
argument_list|(
name|jmsUrl
argument_list|,
name|jmsUser
argument_list|,
name|jmsPassword
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updated
parameter_list|(
name|Dictionary
name|dictionary
parameter_list|)
throws|throws
name|ConfigurationException
block|{
if|if
condition|(
name|dictionary
operator|!=
literal|null
condition|)
block|{
name|jmxUrl
operator|=
operator|(
name|String
operator|)
name|dictionary
operator|.
name|get
argument_list|(
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMX_URL
argument_list|)
expr_stmt|;
if|if
condition|(
name|jmxUrl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A JMS-url must be specified (system property "
operator|+
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMX_URL
argument_list|)
throw|;
block|}
name|jmxUser
operator|=
operator|(
name|String
operator|)
name|dictionary
operator|.
name|get
argument_list|(
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMX_USER
argument_list|)
expr_stmt|;
name|jmxPassword
operator|=
operator|(
name|String
operator|)
name|dictionary
operator|.
name|get
argument_list|(
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMX_PASSWORD
argument_list|)
expr_stmt|;
name|jmsUrl
operator|=
operator|(
name|String
operator|)
name|dictionary
operator|.
name|get
argument_list|(
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMS_URL
argument_list|)
expr_stmt|;
name|jmsUser
operator|=
operator|(
name|String
operator|)
name|dictionary
operator|.
name|get
argument_list|(
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMS_USER
argument_list|)
expr_stmt|;
name|jmsPassword
operator|=
operator|(
name|String
operator|)
name|dictionary
operator|.
name|get
argument_list|(
name|SystemPropertiesConfiguration
operator|.
name|PROPERTY_JMS_PASSWORD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

