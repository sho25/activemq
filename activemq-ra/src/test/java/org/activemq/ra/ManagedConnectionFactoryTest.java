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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
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
name|TopicConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|Referenceable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|BootstrapContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionRequestInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ManagedConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ManagedConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|UnavailableException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|XATerminator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|WorkManager
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ManagedConnectionFactoryTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HOST
init|=
literal|"vm://localhost?broker.persistent=false"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REMOTE_HOST
init|=
literal|"vm://remotehost?broker.persistent=false"
decl_stmt|;
specifier|private
name|ActiveMQManagedConnectionFactory
name|managedConnectionFactory
decl_stmt|;
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQResourceAdapter
name|adapter
init|=
operator|new
name|ActiveMQResourceAdapter
argument_list|()
decl_stmt|;
name|adapter
operator|.
name|setServerUrl
argument_list|(
name|DEFAULT_HOST
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setUserName
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setPassword
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|start
argument_list|(
operator|new
name|BootstrapContext
argument_list|()
block|{
specifier|public
name|WorkManager
name|getWorkManager
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|XATerminator
name|getXATerminator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Timer
name|createTimer
parameter_list|()
throws|throws
name|UnavailableException
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|=
operator|new
name|ActiveMQManagedConnectionFactory
argument_list|()
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setResourceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionFactoryAllocation
parameter_list|()
throws|throws
name|ResourceException
throws|,
name|JMSException
block|{
comment|// Make sure that the ConnectionFactory is asking the connection manager to
comment|// allocate the connection.
specifier|final
name|boolean
name|allocateRequested
index|[]
init|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|}
decl_stmt|;
name|Object
name|cf
init|=
name|managedConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
operator|new
name|ConnectionManagerAdapter
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1699499816530099939L
decl_stmt|;
specifier|public
name|Object
name|allocateConnection
parameter_list|(
name|ManagedConnectionFactory
name|connectionFactory
parameter_list|,
name|ConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|ResourceException
block|{
name|allocateRequested
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
return|return
name|super
operator|.
name|allocateConnection
argument_list|(
name|connectionFactory
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// We should be getting a JMS Connection Factory.
name|assertTrue
argument_list|(
name|cf
operator|instanceof
name|ConnectionFactory
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|(
name|ConnectionFactory
operator|)
name|cf
decl_stmt|;
comment|// Make sure that the connection factory is using the ConnectionManager..
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|allocateRequested
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Make sure that the returned connection is of the expected type.
name|assertTrue
argument_list|(
name|connection
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|connection
operator|instanceof
name|ManagedConnectionProxy
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionFactoryConnectionMatching
parameter_list|()
throws|throws
name|ResourceException
throws|,
name|JMSException
block|{
name|ActiveMQConnectionRequestInfo
name|ri1
init|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
decl_stmt|;
name|ri1
operator|.
name|setServerUrl
argument_list|(
name|DEFAULT_HOST
argument_list|)
expr_stmt|;
name|ri1
operator|.
name|setUserName
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
argument_list|)
expr_stmt|;
name|ri1
operator|.
name|setPassword
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
name|ActiveMQConnectionRequestInfo
name|ri2
init|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
decl_stmt|;
name|ri2
operator|.
name|setServerUrl
argument_list|(
name|REMOTE_HOST
argument_list|)
expr_stmt|;
name|ri2
operator|.
name|setUserName
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
argument_list|)
expr_stmt|;
name|ri2
operator|.
name|setPassword
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ri1
argument_list|,
name|ri2
argument_list|)
expr_stmt|;
name|ManagedConnection
name|connection1
init|=
name|managedConnectionFactory
operator|.
name|createManagedConnection
argument_list|(
literal|null
argument_list|,
name|ri1
argument_list|)
decl_stmt|;
name|ManagedConnection
name|connection2
init|=
name|managedConnectionFactory
operator|.
name|createManagedConnection
argument_list|(
literal|null
argument_list|,
name|ri2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|connection1
operator|!=
name|connection2
argument_list|)
expr_stmt|;
name|HashSet
name|set
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|connection1
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
comment|// Can we match for the first connection?
name|ActiveMQConnectionRequestInfo
name|ri3
init|=
name|ri1
operator|.
name|copy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ri1
operator|!=
name|ri3
operator|&&
name|ri1
operator|.
name|equals
argument_list|(
name|ri3
argument_list|)
argument_list|)
expr_stmt|;
name|ManagedConnection
name|test
init|=
name|managedConnectionFactory
operator|.
name|matchManagedConnections
argument_list|(
name|set
argument_list|,
literal|null
argument_list|,
name|ri3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|connection1
operator|==
name|test
argument_list|)
expr_stmt|;
comment|// Can we match for the second connection?
name|ri3
operator|=
name|ri2
operator|.
name|copy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ri2
operator|!=
name|ri3
operator|&&
name|ri2
operator|.
name|equals
argument_list|(
name|ri3
argument_list|)
argument_list|)
expr_stmt|;
name|test
operator|=
name|managedConnectionFactory
operator|.
name|matchManagedConnections
argument_list|(
name|set
argument_list|,
literal|null
argument_list|,
name|ri2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|connection2
operator|==
name|test
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionFactoryIsSerializableAndReferenceable
parameter_list|()
throws|throws
name|ResourceException
throws|,
name|JMSException
block|{
name|Object
name|cf
init|=
name|managedConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
operator|new
name|ConnectionManagerAdapter
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|instanceof
name|Serializable
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|instanceof
name|Referenceable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testImplementsQueueAndTopicConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|Object
name|cf
init|=
name|managedConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
operator|new
name|ConnectionManagerAdapter
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|instanceof
name|QueueConnectionFactory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cf
operator|instanceof
name|TopicConnectionFactory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

