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
name|proxy
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
name|memory
operator|.
name|UsageManager
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
name|proxy
operator|.
name|ProxyConnector
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

begin_class
specifier|public
class|class
name|ProxyTestSupport
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
name|UsageManager
name|remoteMemoryManager
decl_stmt|;
specifier|protected
name|TransportConnector
name|remoteConnector
decl_stmt|;
specifier|private
name|ProxyConnector
name|proxyConnector
decl_stmt|;
specifier|private
name|ProxyConnector
name|remoteProxyConnector
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setBrokerName
argument_list|(
literal|"broker1"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connector
operator|=
name|service
operator|.
name|addConnector
argument_list|(
name|getLocalURI
argument_list|()
argument_list|)
expr_stmt|;
name|proxyConnector
operator|=
operator|new
name|ProxyConnector
argument_list|()
expr_stmt|;
name|proxyConnector
operator|.
name|setName
argument_list|(
literal|"proxy"
argument_list|)
expr_stmt|;
name|proxyConnector
operator|.
name|setBind
argument_list|(
operator|new
name|URI
argument_list|(
name|getLocalProxyURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|proxyConnector
operator|.
name|setRemote
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fanout:static://"
operator|+
name|getRemoteURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|addProxyConnector
argument_list|(
name|proxyConnector
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setBrokerName
argument_list|(
literal|"broker2"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteConnector
operator|=
name|service
operator|.
name|addConnector
argument_list|(
name|getRemoteURI
argument_list|()
argument_list|)
expr_stmt|;
name|remoteProxyConnector
operator|=
operator|new
name|ProxyConnector
argument_list|()
expr_stmt|;
name|remoteProxyConnector
operator|.
name|setName
argument_list|(
literal|"remoteProxy"
argument_list|)
expr_stmt|;
name|remoteProxyConnector
operator|.
name|setBind
argument_list|(
operator|new
name|URI
argument_list|(
name|getRemoteProxyURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|remoteProxyConnector
operator|.
name|setRemote
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fanout:static://"
operator|+
name|getLocalURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|addProxyConnector
argument_list|(
name|remoteProxyConnector
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
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
name|remoteBroker
operator|=
name|createRemoteBroker
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
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
name|remoteBroker
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
specifier|protected
name|String
name|getRemoteURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:7001"
return|;
block|}
specifier|protected
name|String
name|getLocalURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:6001"
return|;
block|}
specifier|protected
name|String
name|getRemoteProxyURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:7002"
return|;
block|}
specifier|protected
name|String
name|getLocalProxyURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:6002"
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
name|StubConnection
name|createProxyConnection
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
name|proxyConnector
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
name|createRemoteProxyConnection
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
name|remoteProxyConnector
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
block|}
end_class

end_unit

