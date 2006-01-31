begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|vm
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
name|broker
operator|.
name|BrokerFactory
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
name|transport
operator|.
name|MarshallingTransportFilter
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
name|ServiceSupport
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
specifier|public
class|class
name|VMTransportFactory
extends|extends
name|TransportFactory
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|VMTransportFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|public
specifier|static
name|ConcurrentHashMap
name|brokers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|final
specifier|public
specifier|static
name|ConcurrentHashMap
name|connectors
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|final
specifier|public
specifier|static
name|ConcurrentHashMap
name|servers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|BrokerFactoryHandler
name|brokerFactoryHandler
decl_stmt|;
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
return|return
name|VMTransportServer
operator|.
name|configure
argument_list|(
name|doCompositeConnect
argument_list|(
name|location
argument_list|)
argument_list|)
return|;
block|}
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
name|URI
name|brokerURI
decl_stmt|;
name|String
name|host
decl_stmt|;
name|Map
name|options
decl_stmt|;
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
literal|"broker"
operator|.
name|equals
argument_list|(
name|data
operator|.
name|getComponents
argument_list|()
index|[
literal|0
index|]
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|brokerURI
operator|=
name|data
operator|.
name|getComponents
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
name|CompositeData
name|brokerData
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|brokerURI
argument_list|)
decl_stmt|;
name|host
operator|=
operator|(
name|String
operator|)
name|brokerData
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"brokerName"
argument_list|)
expr_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
name|host
operator|=
literal|"localhost"
expr_stmt|;
if|if
condition|(
name|brokerData
operator|.
name|getPath
argument_list|()
operator|!=
literal|null
condition|)
name|host
operator|=
name|data
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|options
operator|=
name|data
operator|.
name|getParameters
argument_list|()
expr_stmt|;
name|location
operator|=
operator|new
name|URI
argument_list|(
literal|"vm://"
operator|+
name|host
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If using the less complex vm://localhost?broker.persistent=true form
try|try
block|{
name|host
operator|=
name|location
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|options
operator|=
name|URISupport
operator|.
name|parseParamters
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|String
name|config
init|=
operator|(
name|String
operator|)
name|options
operator|.
name|remove
argument_list|(
literal|"brokerConfig"
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|brokerURI
operator|=
operator|new
name|URI
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
name|brokerOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"broker."
argument_list|)
decl_stmt|;
name|brokerURI
operator|=
operator|new
name|URI
argument_list|(
literal|"broker://()/"
operator|+
name|host
operator|+
literal|"?"
operator|+
name|URISupport
operator|.
name|createQueryString
argument_list|(
name|brokerOptions
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e1
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e1
argument_list|)
throw|;
block|}
name|location
operator|=
operator|new
name|URI
argument_list|(
literal|"vm://"
operator|+
name|host
argument_list|)
expr_stmt|;
block|}
name|VMTransportServer
name|server
init|=
operator|(
name|VMTransportServer
operator|)
name|servers
operator|.
name|get
argument_list|(
name|host
argument_list|)
decl_stmt|;
comment|// validate the broker is still active
if|if
condition|(
operator|!
name|validateBroker
argument_list|(
name|host
argument_list|)
operator|||
name|server
operator|==
literal|null
condition|)
block|{
name|BrokerService
name|broker
init|=
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|brokerFactoryHandler
operator|!=
literal|null
condition|)
block|{
name|broker
operator|=
name|brokerFactoryHandler
operator|.
name|createBroker
argument_list|(
name|brokerURI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|brokerURI
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|brokers
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|broker
argument_list|)
expr_stmt|;
block|}
name|server
operator|=
operator|(
name|VMTransportServer
operator|)
name|servers
operator|.
name|get
argument_list|(
name|host
argument_list|)
expr_stmt|;
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|(
name|VMTransportServer
operator|)
name|bind
argument_list|(
name|location
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
operator|new
name|TransportConnector
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|,
name|server
argument_list|)
decl_stmt|;
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectors
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|connector
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{}
name|VMTransport
name|vmtransport
init|=
name|server
operator|.
name|connect
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|vmtransport
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Transport
name|transport
init|=
name|vmtransport
decl_stmt|;
if|if
condition|(
name|vmtransport
operator|.
name|isMarshal
argument_list|()
condition|)
block|{
name|HashMap
name|optionsCopy
init|=
operator|new
name|HashMap
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|transport
operator|=
operator|new
name|MarshallingTransportFilter
argument_list|(
name|transport
argument_list|,
name|createWireFormat
argument_list|(
name|options
argument_list|)
argument_list|,
name|createWireFormat
argument_list|(
name|optionsCopy
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|options
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid connect parameters: "
operator|+
name|options
argument_list|)
throw|;
block|}
return|return
name|transport
return|;
block|}
specifier|public
name|TransportServer
name|doBind
parameter_list|(
name|String
name|brokerId
parameter_list|,
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|bind
argument_list|(
name|location
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * @param location      * @return the TransportServer      * @throws IOException      */
specifier|private
name|TransportServer
name|bind
parameter_list|(
name|URI
name|location
parameter_list|,
name|boolean
name|dispose
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|host
init|=
name|location
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"binding to broker: "
operator|+
name|host
argument_list|)
expr_stmt|;
name|VMTransportServer
name|server
init|=
operator|new
name|VMTransportServer
argument_list|(
name|location
argument_list|,
name|dispose
argument_list|)
decl_stmt|;
name|Object
name|currentBoundValue
init|=
name|servers
operator|.
name|get
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentBoundValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"VMTransportServer already bound at: "
operator|+
name|location
argument_list|)
throw|;
block|}
name|servers
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|server
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
specifier|public
specifier|static
name|void
name|stopped
parameter_list|(
name|VMTransportServer
name|server
parameter_list|)
block|{
name|String
name|host
init|=
name|server
operator|.
name|getBindURI
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Shutting down VM connectors for broker: "
operator|+
name|host
argument_list|)
expr_stmt|;
name|servers
operator|.
name|remove
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
operator|(
name|TransportConnector
operator|)
name|connectors
operator|.
name|remove
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
operator|(
name|BrokerService
operator|)
name|brokers
operator|.
name|remove
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|stopped
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Shutting down VM connectors for broker: "
operator|+
name|host
argument_list|)
expr_stmt|;
name|servers
operator|.
name|remove
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
operator|(
name|TransportConnector
operator|)
name|connectors
operator|.
name|remove
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
operator|(
name|BrokerService
operator|)
name|brokers
operator|.
name|remove
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|BrokerFactoryHandler
name|getBrokerFactoryHandler
parameter_list|()
block|{
return|return
name|brokerFactoryHandler
return|;
block|}
specifier|public
name|void
name|setBrokerFactoryHandler
parameter_list|(
name|BrokerFactoryHandler
name|brokerFactoryHandler
parameter_list|)
block|{
name|this
operator|.
name|brokerFactoryHandler
operator|=
name|brokerFactoryHandler
expr_stmt|;
block|}
specifier|private
name|boolean
name|validateBroker
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|brokers
operator|.
name|containsKey
argument_list|(
name|host
argument_list|)
operator|||
name|servers
operator|.
name|containsKey
argument_list|(
name|host
argument_list|)
operator|||
name|connectors
operator|.
name|containsKey
argument_list|(
name|host
argument_list|)
condition|)
block|{
comment|// check the broker is still in the BrokerRegistry
name|TransportConnector
name|connector
init|=
operator|(
name|TransportConnector
operator|)
name|connectors
operator|.
name|get
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|BrokerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
name|host
argument_list|)
operator|==
literal|null
operator|||
operator|(
name|connector
operator|!=
literal|null
operator|&&
name|connector
operator|.
name|getBroker
argument_list|()
operator|.
name|isStopped
argument_list|()
operator|)
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
comment|// clean-up
name|brokers
operator|.
name|remove
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|servers
operator|.
name|remove
argument_list|(
name|host
argument_list|)
expr_stmt|;
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
name|connectors
operator|.
name|remove
argument_list|(
name|host
argument_list|)
expr_stmt|;
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|connector
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

