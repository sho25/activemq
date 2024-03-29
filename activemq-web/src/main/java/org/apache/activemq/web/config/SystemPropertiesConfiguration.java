begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * Configuration based on system-properties.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|SystemPropertiesConfiguration
extends|extends
name|AbstractConfiguration
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_JMS_URL
init|=
literal|"webconsole.jms.url"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_JMS_USER
init|=
literal|"webconsole.jms.user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_JMS_PASSWORD
init|=
literal|"webconsole.jms.password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_JMX_URL
init|=
literal|"webconsole.jmx.url"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_JMX_USER
init|=
literal|"webconsole.jmx.user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_JMX_PASSWORD
init|=
literal|"webconsole.jmx.password"
decl_stmt|;
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
name|String
name|jmsUrl
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_JMS_URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|jmsUrl
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A JMS-url must be specified (system property "
operator|+
name|PROPERTY_JMS_URL
argument_list|)
throw|;
name|String
name|jmsUser
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_JMS_USER
argument_list|)
decl_stmt|;
name|String
name|jmsPassword
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_JMS_PASSWORD
argument_list|)
decl_stmt|;
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
specifier|public
name|Collection
argument_list|<
name|JMXServiceURL
argument_list|>
name|getJmxUrls
parameter_list|()
block|{
name|String
name|jmxUrls
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_JMX_URL
argument_list|)
decl_stmt|;
return|return
name|makeJmxUrls
argument_list|(
name|jmxUrls
argument_list|)
return|;
block|}
specifier|public
name|String
name|getJmxPassword
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_JMX_PASSWORD
argument_list|)
return|;
block|}
specifier|public
name|String
name|getJmxUser
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_JMX_USER
argument_list|)
return|;
block|}
block|}
end_class

end_unit

