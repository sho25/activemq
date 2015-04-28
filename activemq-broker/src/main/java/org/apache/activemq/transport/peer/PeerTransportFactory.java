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
name|peer
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|BrokerFactoryHandler
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
name|vm
operator|.
name|VMTransportFactory
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
name|IdGenerator
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
name|PeerTransportFactory
extends|extends
name|TransportFactory
block|{
specifier|public
specifier|static
specifier|final
name|ConcurrentMap
name|BROKERS
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ConcurrentMap
name|CONNECTORS
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ConcurrentMap
name|SERVERS
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IdGenerator
name|ID_GENERATOR
init|=
operator|new
name|IdGenerator
argument_list|(
literal|"peer-"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Transport
name|doConnect
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|Exception
block|{
name|VMTransportFactory
name|vmTransportFactory
init|=
name|createTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|vmTransportFactory
operator|.
name|doConnect
argument_list|(
name|location
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Transport
name|doCompositeConnect
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|Exception
block|{
name|VMTransportFactory
name|vmTransportFactory
init|=
name|createTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|vmTransportFactory
operator|.
name|doCompositeConnect
argument_list|(
name|location
argument_list|)
return|;
block|}
comment|/**      * @param location      * @return the converted URI      * @throws URISyntaxException      */
specifier|private
name|VMTransportFactory
name|createTransportFactory
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|String
name|group
init|=
name|location
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|broker
init|=
name|URISupport
operator|.
name|stripPrefix
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|group
operator|=
literal|"default"
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|==
literal|null
operator|||
name|broker
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|broker
operator|=
name|ID_GENERATOR
operator|.
name|generateSanitizedId
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|brokerOptions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|brokerOptions
operator|.
name|containsKey
argument_list|(
literal|"persistent"
argument_list|)
condition|)
block|{
name|brokerOptions
operator|.
name|put
argument_list|(
literal|"persistent"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|URI
name|finalLocation
init|=
operator|new
name|URI
argument_list|(
literal|"vm://"
operator|+
name|broker
argument_list|)
decl_stmt|;
specifier|final
name|String
name|finalBroker
init|=
name|broker
decl_stmt|;
specifier|final
name|String
name|finalGroup
init|=
name|group
decl_stmt|;
name|VMTransportFactory
name|rc
init|=
operator|new
name|VMTransportFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Transport
name|doConnect
parameter_list|(
name|URI
name|ignore
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|super
operator|.
name|doConnect
argument_list|(
name|finalLocation
argument_list|)
return|;
block|}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|Transport
name|doCompositeConnect
parameter_list|(
name|URI
name|ignore
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|super
operator|.
name|doCompositeConnect
argument_list|(
name|finalLocation
argument_list|)
return|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|rc
operator|.
name|setBrokerFactoryHandler
argument_list|(
operator|new
name|BrokerFactoryHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|URI
name|brokerURI
parameter_list|)
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
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|service
argument_list|,
name|brokerOptions
argument_list|)
expr_stmt|;
name|service
operator|.
name|setBrokerName
argument_list|(
name|finalBroker
argument_list|)
expr_stmt|;
name|TransportConnector
name|c
init|=
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
decl_stmt|;
name|c
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default?group="
operator|+
name|finalGroup
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|addNetworkConnector
argument_list|(
literal|"multicast://default?group="
operator|+
name|finalGroup
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
literal|"This protocol does not support being bound."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

