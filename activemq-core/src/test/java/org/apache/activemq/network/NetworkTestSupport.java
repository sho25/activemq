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
name|network
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerFactory
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
name|broker
operator|.
name|BrokerRegistry
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|BrokerTestSupport
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
name|broker
operator|.
name|StubConnection
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
name|broker
operator|.
name|TransportConnector
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|memory
operator|.
name|MemoryPersistenceAdapter
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
name|TransportFactory
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_class
specifier|public
class|class
name|NetworkTestSupport
extends|extends
name|BrokerTestSupport
block|{
specifier|protected
name|ArrayList
name|connections
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|TransportConnector
name|connector
decl_stmt|;
specifier|protected
name|PersistenceAdapter
name|remotePersistenceAdapter
decl_stmt|;
specifier|protected
name|BrokerService
name|remoteBroker
decl_stmt|;
specifier|protected
name|SystemUsage
name|remoteMemoryManager
decl_stmt|;
specifier|protected
name|TransportConnector
name|remoteConnector
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|connector
operator|=
name|createConnector
argument_list|()
expr_stmt|;
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
name|remotePersistenceAdapter
operator|=
name|createRemotePersistenceAdapter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|remotePersistenceAdapter
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|=
name|createRemoteBroker
argument_list|(
name|remotePersistenceAdapter
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|bind
argument_list|(
literal|"remotehost"
argument_list|,
name|remoteBroker
argument_list|)
expr_stmt|;
name|remoteConnector
operator|=
name|createRemoteConnector
argument_list|()
expr_stmt|;
name|remoteConnector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return      * @throws Exception      * @throws IOException      * @throws URISyntaxException      */
specifier|protected
name|TransportConnector
name|createRemoteConnector
parameter_list|()
throws|throws
name|Exception
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
return|return
operator|new
name|TransportConnector
argument_list|(
name|remoteBroker
operator|.
name|getBroker
argument_list|()
argument_list|,
name|TransportFactory
operator|.
name|bind
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|new
name|URI
argument_list|(
name|getRemoteURI
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @param value      * @return      * @throws Exception      * @throws IOException      * @throws URISyntaxException      */
specifier|protected
name|TransportConnector
name|createConnector
parameter_list|()
throws|throws
name|Exception
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
return|return
operator|new
name|TransportConnector
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|,
name|TransportFactory
operator|.
name|bind
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|new
name|URI
argument_list|(
name|getLocalURI
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getRemoteURI
parameter_list|()
block|{
return|return
literal|"vm://remotehost"
return|;
block|}
specifier|protected
name|String
name|getLocalURI
parameter_list|()
block|{
return|return
literal|"vm://localhost"
return|;
block|}
specifier|protected
name|PersistenceAdapter
name|createRemotePersistenceAdapter
parameter_list|(
name|boolean
name|clean
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|remotePersistenceAdapter
operator|==
literal|null
operator|||
name|clean
condition|)
block|{
name|remotePersistenceAdapter
operator|=
operator|new
name|MemoryPersistenceAdapter
argument_list|()
expr_stmt|;
block|}
return|return
name|remotePersistenceAdapter
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/localhost?persistent=false&useJmx=false&"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setBrokerName
argument_list|(
literal|"remote"
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|StubConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|connector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
argument_list|)
decl_stmt|;
name|StubConnection
name|connection
init|=
operator|new
name|StubConnection
argument_list|(
name|transport
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
name|StubConnection
name|createRemoteConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|remoteConnector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
argument_list|)
decl_stmt|;
name|StubConnection
name|connection
init|=
operator|new
name|StubConnection
argument_list|(
name|transport
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
name|Transport
name|createTransport
parameter_list|()
throws|throws
name|Exception
block|{
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|connector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|transport
return|;
block|}
specifier|protected
name|Transport
name|createRemoteTransport
parameter_list|()
throws|throws
name|Exception
block|{
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|remoteConnector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|transport
return|;
block|}
comment|/**      * Simulates a broker restart. The memory based persistence adapter is      * reused so that it does not "loose" it's "persistent" messages.      *       * @throws Exception      */
specifier|protected
name|void
name|restartRemoteBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|unbind
argument_list|(
literal|"remotehost"
argument_list|)
expr_stmt|;
name|remoteConnector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remotePersistenceAdapter
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remotePersistenceAdapter
operator|=
name|createRemotePersistenceAdapter
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remotePersistenceAdapter
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|=
name|createRemoteBroker
argument_list|(
name|remotePersistenceAdapter
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|brokerId
init|=
name|remoteBroker
operator|.
name|getBrokerName
argument_list|()
decl_stmt|;
name|remoteConnector
operator|=
operator|new
name|TransportConnector
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|,
name|TransportFactory
operator|.
name|bind
argument_list|(
name|brokerId
argument_list|,
operator|new
name|URI
argument_list|(
name|getRemoteURI
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|remoteConnector
operator|.
name|start
argument_list|()
expr_stmt|;
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|bind
argument_list|(
literal|"remotehost"
argument_list|,
name|remoteBroker
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|connections
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
name|StubConnection
name|connection
init|=
operator|(
name|StubConnection
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|unbind
argument_list|(
literal|"remotehost"
argument_list|)
expr_stmt|;
name|remoteConnector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remotePersistenceAdapter
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

