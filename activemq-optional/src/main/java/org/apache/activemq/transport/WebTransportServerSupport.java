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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|InetAddressUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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

begin_class
specifier|abstract
specifier|public
class|class
name|WebTransportServerSupport
extends|extends
name|TransportServerSupport
block|{
specifier|protected
name|URI
name|bindAddress
decl_stmt|;
specifier|protected
name|Server
name|server
decl_stmt|;
specifier|protected
name|Connector
name|connector
decl_stmt|;
specifier|protected
name|SocketConnectorFactory
name|socketConnectorFactory
decl_stmt|;
specifier|protected
name|String
name|host
decl_stmt|;
specifier|public
name|WebTransportServerSupport
parameter_list|(
name|URI
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|bind
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|bind
init|=
name|getBindLocation
argument_list|()
decl_stmt|;
name|String
name|bindHost
init|=
name|bind
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|bindHost
operator|=
operator|(
name|bindHost
operator|==
literal|null
operator|||
name|bindHost
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|"localhost"
else|:
name|bindHost
expr_stmt|;
name|InetAddress
name|addr
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|bindHost
argument_list|)
decl_stmt|;
name|host
operator|=
name|addr
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
if|if
condition|(
name|addr
operator|.
name|isAnyLocalAddress
argument_list|()
condition|)
block|{
name|host
operator|=
name|InetAddressUtil
operator|.
name|getLocalHostName
argument_list|()
expr_stmt|;
block|}
name|connector
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
name|bindAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|server
operator|.
name|addConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

