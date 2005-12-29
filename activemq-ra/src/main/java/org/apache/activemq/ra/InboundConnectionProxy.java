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
name|ra
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|ActiveMQConnectionMetaData
import|;
end_import

begin_comment
comment|/**  * A {@link Connection} implementation which can be used with the ActiveMQ JCA  * Resource Adapter to publish messages using the same JMS session that is used to dispatch  * messages.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|InboundConnectionProxy
implements|implements
name|Connection
implements|,
name|QueueConnection
implements|,
name|TopicConnection
block|{
specifier|public
name|Session
name|createSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|ackMode
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// TODO we could decide to barf if someone passes in incompatible options
return|return
operator|new
name|InboundSessionProxy
argument_list|()
return|;
block|}
specifier|public
name|QueueSession
name|createQueueSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|ackMode
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// TODO we could decide to barf if someone passes in incompatible options
return|return
operator|new
name|InboundSessionProxy
argument_list|()
return|;
block|}
specifier|public
name|TopicSession
name|createTopicSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|ackMode
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// TODO we could decide to barf if someone passes in incompatible options
return|return
operator|new
name|InboundSessionProxy
argument_list|()
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// the JCA RA is in control of this
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// the JCA RA is in control of this
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// the JCA RA is in control of this
block|}
specifier|public
name|ConnectionMetaData
name|getMetaData
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|ActiveMQConnectionMetaData
operator|.
name|INSTANCE
return|;
block|}
specifier|public
name|String
name|getClientID
parameter_list|()
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"getClientID()"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|setClientID
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"setClient()"
argument_list|)
throw|;
block|}
specifier|public
name|ExceptionListener
name|getExceptionListener
parameter_list|()
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"getExceptionListener()"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|setExceptionListener
parameter_list|(
name|ExceptionListener
name|exceptionListener
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"setExceptionListener()"
argument_list|)
throw|;
block|}
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|s
parameter_list|,
name|ServerSessionPool
name|serverSessionPool
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"createConnectionConsumer()"
argument_list|)
throw|;
block|}
specifier|public
name|ConnectionConsumer
name|createDurableConnectionConsumer
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|s
parameter_list|,
name|String
name|s1
parameter_list|,
name|ServerSessionPool
name|serverSessionPool
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"createDurableConnectionConsumer()"
argument_list|)
throw|;
block|}
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|s
parameter_list|,
name|ServerSessionPool
name|serverSessionPool
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"createConnectionConsumer()"
argument_list|)
throw|;
block|}
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|s
parameter_list|,
name|ServerSessionPool
name|serverSessionPool
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
name|createNotSupported
argument_list|(
literal|"createConnectionConsumer()"
argument_list|)
throw|;
block|}
specifier|protected
name|JMSException
name|createNotSupported
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|JMSException
argument_list|(
literal|"Operation: "
operator|+
name|text
operator|+
literal|" is not supported for this proxy JCA ResourceAdapter provider"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

