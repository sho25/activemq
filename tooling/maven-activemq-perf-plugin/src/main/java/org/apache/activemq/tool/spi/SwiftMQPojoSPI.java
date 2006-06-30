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
name|tool
operator|.
name|spi
package|;
end_package

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
name|jms
operator|.
name|JMSException
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
name|Context
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|SwiftMQPojoSPI
extends|extends
name|ClassLoaderSPIConnectionFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEY_BROKER_URL
init|=
literal|"brokerUrl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_DEST_TYPE
init|=
literal|"destType"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_URL
init|=
literal|"smqp://localhost:4001"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SWIFTMQ_CONTEXT
init|=
literal|"com.swiftmq.jndi.InitialContextFactoryImpl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SMQP
init|=
literal|"com.swiftmq.jms.smqp"
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|instantiateConnectionFactory
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|destType
init|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_DEST_TYPE
argument_list|)
decl_stmt|;
name|ConnectionFactory
name|factory
decl_stmt|;
name|InitialContext
name|context
init|=
name|getInitialContext
argument_list|(
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|destType
operator|!=
literal|null
operator|&&
name|destType
operator|==
literal|"queue"
condition|)
block|{
name|factory
operator|=
operator|(
name|ConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"QueueConnectionFactory"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|factory
operator|=
operator|(
name|ConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"TopicConnectionFactory"
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|configureConnectionFactory
parameter_list|(
name|ConnectionFactory
name|jmsFactory
parameter_list|,
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
specifier|public
name|InitialContext
name|getInitialContext
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_BROKER_URL
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|SWIFTMQ_CONTEXT
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|URL_PKG_PREFIXES
argument_list|,
name|SMQP
argument_list|)
expr_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
operator|&&
name|url
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|DEFAULT_URL
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
operator|new
name|InitialContext
argument_list|(
name|properties
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Error creating InitialContext "
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

