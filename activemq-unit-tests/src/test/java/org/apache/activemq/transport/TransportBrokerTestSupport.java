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
name|transport
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
name|BrokerTest
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

begin_class
specifier|public
specifier|abstract
class|class
name|TransportBrokerTestSupport
extends|extends
name|BrokerTest
block|{
specifier|protected
name|TransportConnector
name|connector
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|StubConnection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|StubConnection
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
block|}
annotation|@
name|Override
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
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|connector
operator|=
name|service
operator|.
name|addConnector
argument_list|(
name|getBindLocation
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
specifier|protected
specifier|abstract
name|String
name|getBindLocation
parameter_list|()
function_decl|;
annotation|@
name|Override
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
argument_list|<
name|StubConnection
argument_list|>
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
try|try
block|{
name|StubConnection
name|connection
init|=
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
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{             }
block|}
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{             }
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|URI
name|getBindURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
return|return
operator|new
name|URI
argument_list|(
name|getBindLocation
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|StubConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|bindURI
init|=
name|getBindURI
argument_list|()
decl_stmt|;
comment|// Note: on platforms like OS X we cannot bind to the actual hostname, so we
comment|// instead use the original host name (typically localhost) to bind to
name|URI
name|actualURI
init|=
name|connector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
decl_stmt|;
name|URI
name|connectURI
init|=
operator|new
name|URI
argument_list|(
name|actualURI
operator|.
name|getScheme
argument_list|()
argument_list|,
name|actualURI
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|bindURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|actualURI
operator|.
name|getPort
argument_list|()
argument_list|,
name|actualURI
operator|.
name|getPath
argument_list|()
argument_list|,
name|bindURI
operator|.
name|getQuery
argument_list|()
argument_list|,
name|bindURI
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|connectURI
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

