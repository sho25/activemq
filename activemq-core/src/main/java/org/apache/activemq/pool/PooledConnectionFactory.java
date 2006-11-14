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
operator|.
name|pool
package|;
end_package

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
name|ActiveMQConnectionFactory
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
name|Service
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
name|IOExceptionSupport
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
name|impl
operator|.
name|GenericObjectPoolFactory
import|;
end_import

begin_comment
comment|/**  * A JMS provider which pools Connection, Session and MessageProducer instances  * so it can be used with tools like Spring's<a  * href="http://activemq.org/Spring+Support">JmsTemplate</a>.  *   *<b>NOTE</b> this implementation is only intended for use when sending  * messages. It does not deal with pooling of consumers; for that look at a  * library like<a href="http://jencks.org/">Jencks</a> such as in<a  * href="http://jencks.org/Message+Driven+POJOs">this example</a>  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PooledConnectionFactory
implements|implements
name|ConnectionFactory
implements|,
name|Service
block|{
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|Map
name|cache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|ObjectPoolFactory
name|poolFactory
decl_stmt|;
specifier|private
name|int
name|maximumActive
init|=
literal|5000
decl_stmt|;
specifier|public
name|PooledConnectionFactory
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PooledConnectionFactory
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PooledConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|connectionFactory
return|;
block|}
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
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
name|ConnectionKey
name|key
init|=
operator|new
name|ConnectionKey
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|ConnectionPool
name|connection
init|=
operator|(
name|ConnectionPool
operator|)
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Now.. we might get a connection, but it might be that we need to
comment|// dump it..
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
name|connection
operator|.
name|expiredCheck
argument_list|()
condition|)
block|{
name|connection
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|ActiveMQConnection
name|delegate
init|=
name|createConnection
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|new
name|ConnectionPool
argument_list|(
name|delegate
argument_list|,
name|getPoolFactory
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PooledConnection
argument_list|(
name|connection
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnection
name|createConnection
parameter_list|(
name|ConnectionKey
name|key
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|key
operator|.
name|getUserName
argument_list|()
operator|==
literal|null
operator|&&
name|key
operator|.
name|getPassword
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|key
operator|.
name|getUserName
argument_list|()
argument_list|,
name|key
operator|.
name|getPassword
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * @see org.apache.activemq.service.Service#start()      */
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
name|createConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|cache
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ConnectionPool
name|connection
init|=
operator|(
name|ConnectionPool
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ObjectPoolFactory
name|getPoolFactory
parameter_list|()
block|{
if|if
condition|(
name|poolFactory
operator|==
literal|null
condition|)
block|{
name|poolFactory
operator|=
name|createPoolFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|poolFactory
return|;
block|}
comment|/**      * Sets the object pool factory used to create individual session pools for      * each connection      */
specifier|public
name|void
name|setPoolFactory
parameter_list|(
name|ObjectPoolFactory
name|poolFactory
parameter_list|)
block|{
name|this
operator|.
name|poolFactory
operator|=
name|poolFactory
expr_stmt|;
block|}
specifier|public
name|int
name|getMaximumActive
parameter_list|()
block|{
return|return
name|maximumActive
return|;
block|}
comment|/**      * Sets the maximum number of active sessions per connection      */
specifier|public
name|void
name|setMaximumActive
parameter_list|(
name|int
name|maximumActive
parameter_list|)
block|{
name|this
operator|.
name|maximumActive
operator|=
name|maximumActive
expr_stmt|;
block|}
specifier|protected
name|ObjectPoolFactory
name|createPoolFactory
parameter_list|()
block|{
return|return
operator|new
name|GenericObjectPoolFactory
argument_list|(
literal|null
argument_list|,
name|maximumActive
argument_list|)
return|;
block|}
block|}
end_class

end_unit

