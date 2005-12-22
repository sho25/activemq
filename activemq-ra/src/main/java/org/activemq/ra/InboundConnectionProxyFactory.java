begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|ra
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
import|;
end_import

begin_comment
comment|/**  * A {@link ConnectionFactory} implementation which creates connections which can  * be used with the ActiveMQ JCA Resource Adapter to publish messages using the  * same underlying JMS session that is used to dispatch messages.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|InboundConnectionProxyFactory
implements|implements
name|ConnectionFactory
implements|,
name|QueueConnectionFactory
implements|,
name|TopicConnectionFactory
block|{
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|new
name|InboundConnectionProxy
argument_list|()
return|;
block|}
specifier|public
name|Connection
name|createConnection
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
name|createConnection
argument_list|()
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|new
name|InboundConnectionProxy
argument_list|()
return|;
block|}
specifier|public
name|QueueConnection
name|createQueueConnection
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
name|createQueueConnection
argument_list|()
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|new
name|InboundConnectionProxy
argument_list|()
return|;
block|}
specifier|public
name|TopicConnection
name|createTopicConnection
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
name|createTopicConnection
argument_list|()
return|;
block|}
block|}
end_class

end_unit

