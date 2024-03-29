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
name|https
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
name|SslContext
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
name|http
operator|.
name|HttpTransportFactory
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

begin_comment
comment|/**  * Factory of HTTPS based transports  */
end_comment

begin_class
specifier|public
class|class
name|HttpsTransportFactory
extends|extends
name|HttpTransportFactory
block|{
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
name|doBind
argument_list|(
name|location
argument_list|)
return|;
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
name|HttpsTransportServer
name|result
init|=
operator|new
name|HttpsTransportServer
argument_list|(
name|location
argument_list|,
name|this
argument_list|,
name|SslContext
operator|.
name|getCurrentSslContext
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|httpOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"http."
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"transport."
argument_list|)
decl_stmt|;
name|result
operator|.
name|setTransportOption
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
name|result
operator|.
name|setHttpOptions
argument_list|(
name|httpOptions
argument_list|)
expr_stmt|;
return|return
name|result
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
name|IOException
block|{
comment|// need to remove options from uri
try|try
block|{
name|URI
name|uri
init|=
name|URISupport
operator|.
name|removeQuery
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
name|options
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"transport."
argument_list|)
decl_stmt|;
name|boolean
name|verifyHostName
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|transportOptions
operator|.
name|containsKey
argument_list|(
literal|"verifyHostName"
argument_list|)
condition|)
block|{
name|verifyHostName
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|transportOptions
operator|.
name|get
argument_list|(
literal|"verifyHostName"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HttpsClientTransport
name|clientTransport
init|=
operator|new
name|HttpsClientTransport
argument_list|(
name|asTextWireFormat
argument_list|(
name|wf
argument_list|)
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|clientTransport
operator|.
name|setVerifyHostName
argument_list|(
name|verifyHostName
argument_list|)
expr_stmt|;
return|return
name|clientTransport
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|MalformedURLException
name|cause
init|=
operator|new
name|MalformedURLException
argument_list|(
literal|"Error removing query on "
operator|+
name|location
argument_list|)
decl_stmt|;
name|cause
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|cause
throw|;
block|}
block|}
comment|// TODO Not sure if there is a better way of removing transport.verifyHostName here?
annotation|@
name|Override
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
name|options
operator|.
name|remove
argument_list|(
literal|"transport.verifyHostName"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|)
return|;
block|}
block|}
end_class

end_unit

