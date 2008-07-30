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
operator|.
name|fanout
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|MutexTransport
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
name|ResponseCorrelator
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
name|transport
operator|.
name|TransportServer
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
name|discovery
operator|.
name|DiscoveryAgent
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
name|discovery
operator|.
name|DiscoveryAgentFactory
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
name|discovery
operator|.
name|DiscoveryTransport
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
name|IntrospectionSupport
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
name|URISupport
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
name|URISupport
operator|.
name|CompositeData
import|;
end_import

begin_class
specifier|public
class|class
name|FanoutTransportFactory
extends|extends
name|TransportFactory
block|{
specifier|public
name|Transport
name|doConnect
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Transport
name|transport
init|=
name|createTransport
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|transport
operator|=
operator|new
name|MutexTransport
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|transport
operator|=
operator|new
name|ResponseCorrelator
argument_list|(
name|transport
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid location: "
operator|+
name|location
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Transport
name|doCompositeConnect
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|createTransport
argument_list|(
name|location
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid location: "
operator|+
name|location
argument_list|)
throw|;
block|}
block|}
comment|/**      * @param location      * @return      * @throws IOException      * @throws URISyntaxException      */
specifier|public
name|Transport
name|createTransport
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|CompositeData
name|compositeData
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|compositeData
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|DiscoveryTransport
name|transport
init|=
operator|new
name|DiscoveryTransport
argument_list|(
name|createTransport
argument_list|(
name|parameters
argument_list|)
argument_list|)
decl_stmt|;
name|DiscoveryAgent
name|discoveryAgent
init|=
name|DiscoveryAgentFactory
operator|.
name|createDiscoveryAgent
argument_list|(
name|compositeData
operator|.
name|getComponents
argument_list|()
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|transport
operator|.
name|setDiscoveryAgent
argument_list|(
name|discoveryAgent
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
specifier|public
name|FanoutTransport
name|createTransport
parameter_list|(
name|Map
name|parameters
parameter_list|)
throws|throws
name|IOException
block|{
name|FanoutTransport
name|transport
init|=
operator|new
name|FanoutTransport
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
specifier|public
name|TransportServer
name|doBind
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid server URI: "
operator|+
name|location
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

