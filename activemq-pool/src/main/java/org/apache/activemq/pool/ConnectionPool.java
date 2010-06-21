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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|transport
operator|.
name|TransportListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|ObjectPoolFactory
import|;
end_import

begin_comment
comment|/**  * Holds a real JMS connection along with the session pools associated with it.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionPool
block|{
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|SessionKey
argument_list|,
name|SessionPool
argument_list|>
name|cache
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|int
name|referenceCount
decl_stmt|;
specifier|private
name|ObjectPoolFactory
name|poolFactory
decl_stmt|;
specifier|private
name|long
name|lastUsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|long
name|firstUsed
init|=
name|lastUsed
decl_stmt|;
specifier|private
name|boolean
name|hasFailed
decl_stmt|;
specifier|private
name|boolean
name|hasExpired
decl_stmt|;
specifier|private
name|int
name|idleTimeout
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|long
name|expiryTimeout
init|=
literal|0l
decl_stmt|;
specifier|public
name|ConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|,
name|ObjectPoolFactory
name|poolFactory
parameter_list|)
block|{
name|this
argument_list|(
name|connection
argument_list|,
operator|new
name|HashMap
argument_list|<
name|SessionKey
argument_list|,
name|SessionPool
argument_list|>
argument_list|()
argument_list|,
name|poolFactory
argument_list|)
expr_stmt|;
comment|// Add a transport Listener so that we can notice if this connection
comment|// should be expired due to
comment|// a connection failure.
name|connection
operator|.
name|addTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{             }
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
synchronized|synchronized
init|(
name|ConnectionPool
operator|.
name|this
init|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"HasFaile=true on :"
operator|+
name|error
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|dumpStack
argument_list|()
expr_stmt|;
name|hasFailed
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{             }
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{             }
block|}
argument_list|)
expr_stmt|;
comment|//
comment|// make sure that we set the hasFailed flag, in case the transport already failed
comment|// prior to the addition of our new TransportListener
comment|//
if|if
condition|(
name|connection
operator|.
name|isTransportFailed
argument_list|()
condition|)
block|{
name|hasFailed
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|public
name|ConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|,
name|Map
argument_list|<
name|SessionKey
argument_list|,
name|SessionPool
argument_list|>
name|cache
parameter_list|,
name|ObjectPoolFactory
name|poolFactory
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|poolFactory
operator|=
name|poolFactory
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|connection
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
name|started
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|e
operator|)
throw|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|ActiveMQConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
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
name|SessionKey
name|key
init|=
operator|new
name|SessionKey
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
decl_stmt|;
name|SessionPool
name|pool
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|pool
operator|=
name|createSessionPool
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|pool
argument_list|)
expr_stmt|;
block|}
name|PooledSession
name|session
init|=
name|pool
operator|.
name|borrowSession
argument_list|()
decl_stmt|;
return|return
name|session
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Iterator
argument_list|<
name|SessionPool
argument_list|>
name|i
init|=
name|cache
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SessionPool
name|pool
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|pool
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                     }
block|}
block|}
finally|finally
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
finally|finally
block|{
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|incrementReferenceCount
parameter_list|()
block|{
name|referenceCount
operator|++
expr_stmt|;
name|lastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|decrementReferenceCount
parameter_list|()
block|{
name|referenceCount
operator|--
expr_stmt|;
name|lastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|expiredCheck
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return true if this connection has expired.      */
specifier|public
specifier|synchronized
name|boolean
name|expiredCheck
parameter_list|()
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|hasExpired
condition|)
block|{
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|hasFailed
operator|||
operator|(
name|idleTimeout
operator|>
literal|0
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|lastUsed
operator|+
name|idleTimeout
operator|)
operator|||
name|expiryTimeout
operator|>
literal|0
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|firstUsed
operator|+
name|expiryTimeout
condition|)
block|{
name|hasExpired
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|getIdleTimeout
parameter_list|()
block|{
return|return
name|idleTimeout
return|;
block|}
specifier|public
name|void
name|setIdleTimeout
parameter_list|(
name|int
name|idleTimeout
parameter_list|)
block|{
name|this
operator|.
name|idleTimeout
operator|=
name|idleTimeout
expr_stmt|;
block|}
specifier|protected
name|SessionPool
name|createSessionPool
parameter_list|(
name|SessionKey
name|key
parameter_list|)
block|{
return|return
operator|new
name|SessionPool
argument_list|(
name|this
argument_list|,
name|key
argument_list|,
name|poolFactory
operator|.
name|createPool
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setExpiryTimeout
parameter_list|(
name|long
name|expiryTimeout
parameter_list|)
block|{
name|this
operator|.
name|expiryTimeout
operator|=
name|expiryTimeout
expr_stmt|;
block|}
specifier|public
name|long
name|getExpiryTimeout
parameter_list|()
block|{
return|return
name|expiryTimeout
return|;
block|}
block|}
end_class

end_unit

