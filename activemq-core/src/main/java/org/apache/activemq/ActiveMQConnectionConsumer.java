begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
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
name|ServerSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ServerSessionPool
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|command
operator|.
name|ConsumerInfo
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
name|command
operator|.
name|MessageDispatch
import|;
end_import

begin_comment
comment|/**  * For application servers,<CODE>Connection</CODE> objects provide a special  * facility for creating a<CODE>ConnectionConsumer</CODE> (optional). The  * messages it is to consume are specified by a<CODE>Destination</CODE> and  * a message selector. In addition, a<CODE>ConnectionConsumer</CODE> must be  * given a<CODE>ServerSessionPool</CODE> to use for processing its messages.  *<p/>  *<P>  * Normally, when traffic is light, a<CODE>ConnectionConsumer</CODE> gets a  *<CODE>ServerSession</CODE> from its pool, loads it with a single message,  * and starts it. As traffic picks up, messages can back up. If this happens, a  *<CODE>ConnectionConsumer</CODE> can load each<CODE>ServerSession</CODE>  * with more than one message. This reduces the thread context switches and  * minimizes resource use at the expense of some serialization of message  * processing.  *  * @see javax.jms.Connection#createConnectionConsumer  * @see javax.jms.Connection#createDurableConnectionConsumer  * @see javax.jms.QueueConnection#createConnectionConsumer  * @see javax.jms.TopicConnection#createConnectionConsumer  * @see javax.jms.TopicConnection#createDurableConnectionConsumer  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionConsumer
implements|implements
name|ConnectionConsumer
implements|,
name|ActiveMQDispatcher
block|{
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|ServerSessionPool
name|sessionPool
decl_stmt|;
specifier|private
name|ConsumerInfo
name|consumerInfo
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|protected
specifier|final
name|List
name|messageQueue
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|LinkedList
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Create a ConnectionConsumer      *      * @param theConnection      * @param theSessionPool      * @param theConsumerInfo      * @param theMaximumMessages      * @throws JMSException      */
specifier|protected
name|ActiveMQConnectionConsumer
parameter_list|(
name|ActiveMQConnection
name|theConnection
parameter_list|,
name|ServerSessionPool
name|theSessionPool
parameter_list|,
name|ConsumerInfo
name|theConsumerInfo
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|connection
operator|=
name|theConnection
expr_stmt|;
name|this
operator|.
name|sessionPool
operator|=
name|theSessionPool
expr_stmt|;
name|this
operator|.
name|consumerInfo
operator|=
name|theConsumerInfo
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|addConnectionConsumer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|addDispatcher
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|syncSendPacket
argument_list|(
name|this
operator|.
name|consumerInfo
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the server session pool associated with this connection consumer.      *      * @return the server session pool used by this connection consumer      * @throws JMSException if the JMS provider fails to get the server session pool      *                      associated with this consumer due to some internal error.      */
specifier|public
name|ServerSessionPool
name|getServerSessionPool
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The Connection Consumer is closed"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|sessionPool
return|;
block|}
comment|/**      * Closes the connection consumer.      *<p/>      *<P>      * Since a provider may allocate some resources on behalf of a connection      * consumer outside the Java virtual machine, clients should close these      * resources when they are not needed. Relying on garbage collection to      * eventually reclaim these resources may not be timely enough.      *      * @throws JMSException      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|dispose
argument_list|()
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|asyncSendPacket
argument_list|(
name|this
operator|.
name|consumerInfo
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|this
operator|.
name|connection
operator|.
name|removeDispatcher
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|removeConnectionConsumer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|dispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
try|try
block|{
name|messageDispatch
operator|.
name|setConsumer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ServerSession
name|serverSession
init|=
name|sessionPool
operator|.
name|getServerSession
argument_list|()
decl_stmt|;
name|Session
name|s
init|=
name|serverSession
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|ActiveMQSession
name|session
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|s
operator|instanceof
name|ActiveMQSession
condition|)
block|{
name|session
operator|=
operator|(
name|ActiveMQSession
operator|)
name|s
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|instanceof
name|ActiveMQTopicSession
condition|)
block|{
name|ActiveMQTopicSession
name|topicSession
init|=
operator|(
name|ActiveMQTopicSession
operator|)
name|s
decl_stmt|;
name|session
operator|=
operator|(
name|ActiveMQSession
operator|)
name|topicSession
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|instanceof
name|ActiveMQQueueSession
condition|)
block|{
name|ActiveMQQueueSession
name|queueSession
init|=
operator|(
name|ActiveMQQueueSession
operator|)
name|s
decl_stmt|;
name|session
operator|=
operator|(
name|ActiveMQSession
operator|)
name|queueSession
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|connection
operator|.
name|onAsyncException
argument_list|(
operator|new
name|JMSException
argument_list|(
literal|"Session pool provided an invalid session type: "
operator|+
name|s
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|session
operator|.
name|dispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
name|serverSession
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|connection
operator|.
name|onAsyncException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ActiveMQConnectionConsumer { value="
operator|+
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" }"
return|;
block|}
block|}
end_class

end_unit

