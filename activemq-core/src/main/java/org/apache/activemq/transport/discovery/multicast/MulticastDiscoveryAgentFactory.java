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
name|discovery
operator|.
name|multicast
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

begin_class
specifier|public
class|class
name|MulticastDiscoveryAgentFactory
extends|extends
name|DiscoveryAgentFactory
block|{
comment|//See AMQ-1489. There's something wrong here but it is difficult to tell what.
comment|//It looks like to actually set the discovery URI you have to use something like
comment|//<transportConnector uri="..." discoveryUri="multicast://239.3.7.0:37000?discoveryURI=multicast://239.3.7.0:37000" />
comment|// or
comment|//<networkConnector name="..." uri="multicast://239.3.7.0:37000?discoveryURI=multicast://239.3.7.0:37000">
specifier|protected
name|DiscoveryAgent
name|doCreateDiscoveryAgent
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Map
name|options
init|=
name|URISupport
operator|.
name|parseParamters
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|MulticastDiscoveryAgent
name|rc
init|=
operator|new
name|MulticastDiscoveryAgent
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setGroup
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
comment|// allow the discoveryURI to be set via a query argument on the URI
comment|// ?discoveryURI=someURI
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|rc
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Could not create discovery agent: "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

