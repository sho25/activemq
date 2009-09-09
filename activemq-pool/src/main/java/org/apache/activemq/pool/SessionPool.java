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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|JMSExceptionSupport
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
name|ObjectPool
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
name|PoolableObjectFactory
import|;
end_import

begin_comment
comment|/**  * Represents the session pool for a given JMS connection.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|SessionPool
implements|implements
name|PoolableObjectFactory
block|{
specifier|private
name|ConnectionPool
name|connectionPool
decl_stmt|;
specifier|private
name|SessionKey
name|key
decl_stmt|;
specifier|private
name|ObjectPool
name|sessionPool
decl_stmt|;
specifier|public
name|SessionPool
parameter_list|(
name|ConnectionPool
name|connectionPool
parameter_list|,
name|SessionKey
name|key
parameter_list|,
name|ObjectPool
name|sessionPool
parameter_list|)
block|{
name|this
operator|.
name|connectionPool
operator|=
name|connectionPool
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|sessionPool
operator|=
name|sessionPool
expr_stmt|;
name|sessionPool
operator|.
name|setFactory
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|sessionPool
operator|!=
literal|null
condition|)
block|{
name|sessionPool
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|sessionPool
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|PooledSession
name|borrowSession
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|Object
name|object
init|=
name|getSessionPool
argument_list|()
operator|.
name|borrowObject
argument_list|()
decl_stmt|;
return|return
operator|(
name|PooledSession
operator|)
name|object
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|returnSession
parameter_list|(
name|PooledSession
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// lets check if we are already closed
name|getConnection
argument_list|()
expr_stmt|;
try|try
block|{
name|getSessionPool
argument_list|()
operator|.
name|returnObject
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to return session to pool: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|invalidateSession
parameter_list|(
name|PooledSession
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|getSessionPool
argument_list|()
operator|.
name|invalidateObject
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to invalidate session: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// PoolableObjectFactory methods
comment|// -------------------------------------------------------------------------
specifier|public
name|Object
name|makeObject
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|PooledSession
argument_list|(
name|createSession
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|void
name|destroyObject
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|Exception
block|{
name|PooledSession
name|session
init|=
operator|(
name|PooledSession
operator|)
name|o
decl_stmt|;
name|session
operator|.
name|getInternalSession
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|validateObject
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|activateObject
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|passivateObject
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|Exception
block|{     }
comment|// Implemention methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|ObjectPool
name|getSessionPool
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|sessionPool
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
return|return
name|sessionPool
return|;
block|}
specifier|protected
name|ActiveMQConnection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|connectionPool
operator|.
name|getConnection
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQSession
name|createSession
parameter_list|()
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
block|}
end_class

end_unit

