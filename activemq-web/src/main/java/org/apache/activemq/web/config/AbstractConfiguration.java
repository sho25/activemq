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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  * Base class for configurations.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractConfiguration
implements|implements
name|WebConsoleConfiguration
block|{
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getJmxPassword
parameter_list|()
block|{
return|return
literal|null
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
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getJmxUser
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * Creates the ActiveMQ-ConnectionFactory. 	 *  	 * @param jmsUrl 	 *            not<code>null</code> 	 * @param jmsUser 	 *<code>null</code> if no authentication 	 * @param jmsPassword 	 *<code>null</code> is ok 	 * @return not<code>null</code> 	 */
specifier|protected
name|ConnectionFactory
name|makeConnectionFactory
parameter_list|(
name|String
name|jmsUrl
parameter_list|,
name|String
name|jmsUser
parameter_list|,
name|String
name|jmsPassword
parameter_list|)
block|{
if|if
condition|(
name|jmsUser
operator|!=
literal|null
operator|&&
name|jmsUser
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|jmsUser
argument_list|,
name|jmsPassword
argument_list|,
name|jmsUrl
argument_list|)
return|;
else|else
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|jmsUrl
argument_list|)
return|;
block|}
comment|/** 	 * Splits the JMX-Url string into a series of JMSServiceURLs. 	 *  	 * @param jmxUrls 	 *            the JMX-url, multiple URLs are separated by commas. 	 * @return not<code>null</code>, contains at least one element. 	 */
specifier|protected
name|Collection
argument_list|<
name|JMXServiceURL
argument_list|>
name|makeJmxUrls
parameter_list|(
name|String
name|jmxUrls
parameter_list|)
block|{
name|String
index|[]
name|urls
init|=
name|jmxUrls
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|urls
operator|==
literal|null
operator|||
name|urls
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|urls
operator|=
operator|new
name|String
index|[]
block|{
name|jmxUrls
block|}
expr_stmt|;
block|}
try|try
block|{
name|Collection
argument_list|<
name|JMXServiceURL
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|JMXServiceURL
argument_list|>
argument_list|(
name|jmxUrls
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|url
range|:
name|urls
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|JMXServiceURL
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid JMX-url"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

