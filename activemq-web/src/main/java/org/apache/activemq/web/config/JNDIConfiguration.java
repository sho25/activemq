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

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_comment
comment|/**  * Configuration based on JNDI values.  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|JNDIConfiguration
extends|extends
name|AbstractConfiguration
block|{
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMS_CONNECTION_FACTORY
init|=
literal|"java:comp/env/jms/connectionFactory"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMS_URL
init|=
literal|"java:comp/env/jms/url"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMS_USER
init|=
literal|"java:comp/env/jms/user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMS_PASSWORD
init|=
literal|"java:comp/env/jms/password"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMX_URL
init|=
literal|"java:comp/env/jmx/url"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMX_USER
init|=
literal|"java:comp/env/jmx/user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JNDI_JMX_PASSWORD
init|=
literal|"java:comp/env/jmx/password"
decl_stmt|;
specifier|private
name|InitialContext
name|context
decl_stmt|;
specifier|public
name|JNDIConfiguration
parameter_list|()
throws|throws
name|NamingException
block|{
name|this
operator|.
name|context
operator|=
operator|new
name|InitialContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|JNDIConfiguration
parameter_list|(
name|InitialContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
try|try
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|(
name|ConnectionFactory
operator|)
name|this
operator|.
name|context
operator|.
name|lookup
argument_list|(
name|JNDI_JMS_CONNECTION_FACTORY
argument_list|)
decl_stmt|;
return|return
name|connectionFactory
return|;
block|}
catch|catch
parameter_list|(
name|NameNotFoundException
name|e
parameter_list|)
block|{
comment|// try to find an url
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|jmsUrl
init|=
operator|(
name|String
operator|)
name|this
operator|.
name|context
operator|.
name|lookup
argument_list|(
name|JNDI_JMS_URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|jmsUrl
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
name|JNDI_JMS_URL
argument_list|)
throw|;
block|}
name|String
name|jmsUser
init|=
name|getJndiString
argument_list|(
name|JNDI_JMS_USER
argument_list|)
decl_stmt|;
name|String
name|jmsPassword
init|=
name|getJndiString
argument_list|(
name|JNDI_JMS_PASSWORD
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
catch|catch
parameter_list|(
name|NameNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Neither a ConnectionFactory nor a JMS-url were specified"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
name|getJndiString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|String
operator|)
name|this
operator|.
name|context
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
name|getJndiString
argument_list|(
name|JNDI_JMX_URL
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
name|getJndiString
argument_list|(
name|JNDI_JMX_USER
argument_list|)
return|;
block|}
specifier|public
name|String
name|getJmxUser
parameter_list|()
block|{
return|return
name|getJndiString
argument_list|(
name|JNDI_JMX_PASSWORD
argument_list|)
return|;
block|}
block|}
end_class

end_unit

