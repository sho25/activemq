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
name|MalformedURLException
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
name|net
operator|.
name|UnknownHostException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
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
name|FactoryFinder
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
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
name|wireformat
operator|.
name|WireFormatFactory
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|TransportFactory
block|{
specifier|private
specifier|static
specifier|final
name|FactoryFinder
name|TRANSPORT_FACTORY_FINDER
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/transport/"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FactoryFinder
name|WIREFORMAT_FACTORY_FINDER
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/wireformat/"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|TransportFactory
argument_list|>
name|TRANSPORT_FACTORYS
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|TransportFactory
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WRITE_TIMEOUT_FILTER
init|=
literal|"soWriteTimeout"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|THREAD_NAME_FILTER
init|=
literal|"threadName"
decl_stmt|;
specifier|public
specifier|abstract
name|TransportServer
name|doBind
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
name|Transport
name|doConnect
parameter_list|(
name|URI
name|location
parameter_list|,
name|Executor
name|ex
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|doConnect
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|public
name|Transport
name|doCompositeConnect
parameter_list|(
name|URI
name|location
parameter_list|,
name|Executor
name|ex
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|doCompositeConnect
argument_list|(
name|location
argument_list|)
return|;
block|}
comment|/**      * Creates a normal transport.      *      * @param location      * @return the transport      * @throws Exception      */
specifier|public
specifier|static
name|Transport
name|connect
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportFactory
name|tf
init|=
name|findTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|doConnect
argument_list|(
name|location
argument_list|)
return|;
block|}
comment|/**      * Creates a normal transport.      *      * @param location      * @param ex      * @return the transport      * @throws Exception      */
specifier|public
specifier|static
name|Transport
name|connect
parameter_list|(
name|URI
name|location
parameter_list|,
name|Executor
name|ex
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportFactory
name|tf
init|=
name|findTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|doConnect
argument_list|(
name|location
argument_list|,
name|ex
argument_list|)
return|;
block|}
comment|/**      * Creates a slimmed down transport that is more efficient so that it can be      * used by composite transports like reliable and HA.      *      * @param location      * @return the Transport      * @throws Exception      */
specifier|public
specifier|static
name|Transport
name|compositeConnect
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportFactory
name|tf
init|=
name|findTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|doCompositeConnect
argument_list|(
name|location
argument_list|)
return|;
block|}
comment|/**      * Creates a slimmed down transport that is more efficient so that it can be      * used by composite transports like reliable and HA.      *      * @param location      * @param ex      * @return the Transport      * @throws Exception      */
specifier|public
specifier|static
name|Transport
name|compositeConnect
parameter_list|(
name|URI
name|location
parameter_list|,
name|Executor
name|ex
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportFactory
name|tf
init|=
name|findTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|doCompositeConnect
argument_list|(
name|location
argument_list|,
name|ex
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|TransportServer
name|bind
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|TransportFactory
name|tf
init|=
name|findTransportFactory
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|doBind
argument_list|(
name|location
argument_list|)
return|;
block|}
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
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
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
name|options
operator|.
name|containsKey
argument_list|(
literal|"wireFormat.host"
argument_list|)
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
literal|"wireFormat.host"
argument_list|,
name|location
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|WireFormat
name|wf
init|=
name|createWireFormat
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Transport
name|transport
init|=
name|createTransport
argument_list|(
name|location
argument_list|,
name|wf
argument_list|)
decl_stmt|;
name|Transport
name|rc
init|=
name|configure
argument_list|(
name|transport
argument_list|,
name|wf
argument_list|,
name|options
argument_list|)
decl_stmt|;
comment|//remove auto
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"auto."
argument_list|)
expr_stmt|;
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
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
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
name|WireFormat
name|wf
init|=
name|createWireFormat
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Transport
name|transport
init|=
name|createTransport
argument_list|(
name|location
argument_list|,
name|wf
argument_list|)
decl_stmt|;
name|Transport
name|rc
init|=
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
name|wf
argument_list|,
name|options
argument_list|)
decl_stmt|;
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
comment|/**       * Allow registration of a transport factory without wiring via META-INF classes      * @param scheme      * @param tf      */
specifier|public
specifier|static
name|void
name|registerTransportFactory
parameter_list|(
name|String
name|scheme
parameter_list|,
name|TransportFactory
name|tf
parameter_list|)
block|{
name|TRANSPORT_FACTORYS
operator|.
name|put
argument_list|(
name|scheme
argument_list|,
name|tf
argument_list|)
expr_stmt|;
block|}
comment|/**      * Factory method to create a new transport      *      * @throws IOException      * @throws UnknownHostException      */
specifier|protected
name|Transport
name|createTransport
parameter_list|(
name|URI
name|location
parameter_list|,
name|WireFormat
name|wf
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|UnknownHostException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"createTransport() method not implemented!"
argument_list|)
throw|;
block|}
comment|/**      * @param location      * @return      * @throws IOException      */
specifier|public
specifier|static
name|TransportFactory
name|findTransportFactory
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|scheme
init|=
name|location
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheme
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Transport not scheme specified: ["
operator|+
name|location
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|TransportFactory
name|tf
init|=
name|TRANSPORT_FACTORYS
operator|.
name|get
argument_list|(
name|scheme
argument_list|)
decl_stmt|;
if|if
condition|(
name|tf
operator|==
literal|null
condition|)
block|{
comment|// Try to load if from a META-INF property.
try|try
block|{
name|tf
operator|=
operator|(
name|TransportFactory
operator|)
name|TRANSPORT_FACTORY_FINDER
operator|.
name|newInstance
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|TRANSPORT_FACTORYS
operator|.
name|put
argument_list|(
name|scheme
argument_list|,
name|tf
argument_list|)
expr_stmt|;
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
literal|"Transport scheme NOT recognized: ["
operator|+
name|scheme
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|tf
return|;
block|}
specifier|protected
name|WireFormat
name|createWireFormat
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|WireFormatFactory
name|factory
init|=
name|createWireFormatFactory
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|WireFormat
name|format
init|=
name|factory
operator|.
name|createWireFormat
argument_list|()
decl_stmt|;
return|return
name|format
return|;
block|}
specifier|protected
name|WireFormatFactory
name|createWireFormatFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|wireFormat
init|=
name|options
operator|.
name|remove
argument_list|(
literal|"wireFormat"
argument_list|)
decl_stmt|;
if|if
condition|(
name|wireFormat
operator|==
literal|null
condition|)
block|{
name|wireFormat
operator|=
name|getDefaultWireFormatType
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|WireFormatFactory
name|wff
init|=
operator|(
name|WireFormatFactory
operator|)
name|WIREFORMAT_FACTORY_FINDER
operator|.
name|newInstance
argument_list|(
name|wireFormat
argument_list|)
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|wff
argument_list|,
name|options
argument_list|,
literal|"wireFormat."
argument_list|)
expr_stmt|;
return|return
name|wff
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
literal|"Could not create wire format factory for: "
operator|+
name|wireFormat
operator|+
literal|", reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
name|getDefaultWireFormatType
parameter_list|()
block|{
return|return
literal|"default"
return|;
block|}
comment|/**      * Fully configures and adds all need transport filters so that the      * transport can be used by the JMS client.      *      * @param transport      * @param wf      * @param options      * @return      * @throws Exception      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|Transport
name|configure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|wf
parameter_list|,
name|Map
name|options
parameter_list|)
throws|throws
name|Exception
block|{
name|transport
operator|=
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
name|wf
argument_list|,
name|options
argument_list|)
expr_stmt|;
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
comment|/**      * Fully configures and adds all need transport filters so that the      * transport can be used by the ActiveMQ message broker. The main difference      * between this and the configure() method is that the broker does not issue      * requests to the client so the ResponseCorrelator is not needed.      *      * @param transport      * @param format      * @param options      * @return      * @throws Exception      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|Transport
name|serverConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|HashMap
name|options
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|options
operator|.
name|containsKey
argument_list|(
name|THREAD_NAME_FILTER
argument_list|)
condition|)
block|{
name|transport
operator|=
operator|new
name|ThreadNameFilter
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
name|transport
operator|=
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|transport
operator|=
operator|new
name|MutexTransport
argument_list|(
name|transport
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
comment|/**      * Similar to configure(...) but this avoid adding in the MutexTransport and      * ResponseCorrelator transport layers so that the resulting transport can      * more efficiently be used as part of a composite transport.      *      * @param transport      * @param format      * @param options      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|Transport
name|compositeConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|containsKey
argument_list|(
name|WRITE_TIMEOUT_FILTER
argument_list|)
condition|)
block|{
name|transport
operator|=
operator|new
name|WriteTimeoutFilter
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|String
name|soWriteTimeout
init|=
operator|(
name|String
operator|)
name|options
operator|.
name|remove
argument_list|(
name|WRITE_TIMEOUT_FILTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|soWriteTimeout
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|WriteTimeoutFilter
operator|)
name|transport
operator|)
operator|.
name|setWriteTimeout
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|soWriteTimeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|protected
name|String
name|getOption
parameter_list|(
name|Map
name|options
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|rc
init|=
operator|(
name|String
operator|)
name|options
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|rc
operator|=
name|def
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit

