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
name|pool
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
name|ActiveMQConnection
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
name|ActiveMQSession
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
name|AlreadyClosedException
import|;
end_import

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
name|ConnectionConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionMetaData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
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
name|Queue
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
name|QueueSession
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
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|TopicSession
import|;
end_import

begin_comment
comment|/**  * Represents a proxy {@link Connection} which is-a {@link TopicConnection} and  * {@link QueueConnection} which is pooled and on {@link #close()} will return  * itself to the sessionPool.  *   *<b>NOTE</b> this implementation is only intended for use when sending  * messages.  * It does not deal with pooling of consumers; for that look at a library like   *<a href="http://jencks.org/">Jencks</a> such as in  *<a href="http://jencks.org/Message+Driven+POJOs">this example</a>  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PooledConnection
implements|implements
name|TopicConnection
implements|,
name|QueueConnection
block|{
specifier|private
name|ConnectionPool
name|pool
decl_stmt|;
specifier|private
name|boolean
name|stopped
decl_stmt|;
specifier|public
name|PooledConnection
parameter_list|(
name|ConnectionPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|pool
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
comment|/**      * Factory method to create a new instance.      */
specifier|public
name|PooledConnection
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|PooledConnection
argument_list|(
name|pool
argument_list|)
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|this
operator|.
name|pool
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|pool
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|pool
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
name|assertNotClosed
argument_list|()
expr_stmt|;
name|pool
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|stopped
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|selector
parameter_list|,
name|ServerSessionPool
name|serverSessionPool
parameter_list|,
name|int
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|createConnectionConsumer
argument_list|(
name|destination
argument_list|,
name|selector
argument_list|,
name|serverSessionPool
argument_list|,
name|maxMessages
argument_list|)
return|;
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
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|createConnectionConsumer
argument_list|(
name|topic
argument_list|,
name|s
argument_list|,
name|serverSessionPool
argument_list|,
name|maxMessages
argument_list|)
return|;
block|}
specifier|public
name|ConnectionConsumer
name|createDurableConnectionConsumer
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|selector
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
return|return
name|getConnection
argument_list|()
operator|.
name|createDurableConnectionConsumer
argument_list|(
name|topic
argument_list|,
name|selector
argument_list|,
name|s1
argument_list|,
name|serverSessionPool
argument_list|,
name|i
argument_list|)
return|;
block|}
specifier|public
name|String
name|getClientID
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getClientID
argument_list|()
return|;
block|}
specifier|public
name|ExceptionListener
name|getExceptionListener
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getExceptionListener
argument_list|()
return|;
block|}
specifier|public
name|ConnectionMetaData
name|getMetaData
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getMetaData
argument_list|()
return|;
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
name|getConnection
argument_list|()
operator|.
name|setExceptionListener
argument_list|(
name|exceptionListener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setClientID
parameter_list|(
name|String
name|clientID
parameter_list|)
throws|throws
name|JMSException
block|{
name|getConnection
argument_list|()
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|selector
parameter_list|,
name|ServerSessionPool
name|serverSessionPool
parameter_list|,
name|int
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|createConnectionConsumer
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|,
name|serverSessionPool
argument_list|,
name|maxMessages
argument_list|)
return|;
block|}
comment|// Session factory methods
comment|// -------------------------------------------------------------------------
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
return|return
operator|(
name|QueueSession
operator|)
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
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
return|return
operator|(
name|TopicSession
operator|)
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
return|;
block|}
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
return|return
name|pool
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
name|ActiveMQConnection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|assertNotClosed
argument_list|()
expr_stmt|;
return|return
name|pool
operator|.
name|getConnection
argument_list|()
return|;
block|}
specifier|protected
name|void
name|assertNotClosed
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|stopped
operator|||
name|pool
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|()
throw|;
block|}
block|}
specifier|protected
name|ActiveMQSession
name|createSession
parameter_list|(
name|SessionKey
name|key
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|ActiveMQSession
operator|)
name|getConnection
argument_list|()
operator|.
name|createSession
argument_list|(
name|key
operator|.
name|isTransacted
argument_list|()
argument_list|,
name|key
operator|.
name|getAckMode
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PooledConnection { "
operator|+
name|pool
operator|+
literal|" }"
return|;
block|}
block|}
end_class

end_unit

