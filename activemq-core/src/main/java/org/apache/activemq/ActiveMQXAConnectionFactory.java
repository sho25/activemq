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
package|;
end_package

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
name|jms
operator|.
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAQueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAQueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XATopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XATopicConnectionFactory
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
name|management
operator|.
name|JMSStatsImpl
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
name|transport
operator|.
name|Transport
import|;
end_import

begin_comment
comment|/**  * A factory of {@link XAConnection} instances  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQXAConnectionFactory
extends|extends
name|ActiveMQConnectionFactory
implements|implements
name|XAConnectionFactory
implements|,
name|XAQueueConnectionFactory
implements|,
name|XATopicConnectionFactory
block|{
specifier|public
name|ActiveMQXAConnectionFactory
parameter_list|()
block|{     }
specifier|public
name|ActiveMQXAConnectionFactory
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|brokerURL
parameter_list|)
block|{
name|super
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|brokerURL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQXAConnectionFactory
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|,
name|URI
name|brokerURL
parameter_list|)
block|{
name|super
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|brokerURL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQXAConnectionFactory
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|super
argument_list|(
name|brokerURL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQXAConnectionFactory
parameter_list|(
name|URI
name|brokerURL
parameter_list|)
block|{
name|super
argument_list|(
name|brokerURL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XAConnection
name|createXAConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XAConnection
operator|)
name|createActiveMQConnection
argument_list|()
return|;
block|}
specifier|public
name|XAConnection
name|createXAConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XAConnection
operator|)
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|XAQueueConnection
name|createXAQueueConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XAQueueConnection
operator|)
name|createActiveMQConnection
argument_list|()
return|;
block|}
specifier|public
name|XAQueueConnection
name|createXAQueueConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XAQueueConnection
operator|)
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|XATopicConnection
name|createXATopicConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XATopicConnection
operator|)
name|createActiveMQConnection
argument_list|()
return|;
block|}
specifier|public
name|XATopicConnection
name|createXATopicConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|XATopicConnection
operator|)
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnection
name|createActiveMQConnection
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|JMSStatsImpl
name|stats
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQXAConnection
name|connection
init|=
operator|new
name|ActiveMQXAConnection
argument_list|(
name|transport
argument_list|,
name|getClientIdGenerator
argument_list|()
argument_list|,
name|stats
argument_list|)
decl_stmt|;
return|return
name|connection
return|;
block|}
block|}
end_class

end_unit

