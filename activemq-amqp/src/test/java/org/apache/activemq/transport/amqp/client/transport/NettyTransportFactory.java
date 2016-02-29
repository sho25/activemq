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
name|amqp
operator|.
name|client
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
name|amqp
operator|.
name|client
operator|.
name|util
operator|.
name|PropertyUtil
import|;
end_import

begin_comment
comment|/**  * Factory for creating the Netty based TCP Transport.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NettyTransportFactory
block|{
specifier|private
name|NettyTransportFactory
parameter_list|()
block|{}
comment|/**      * Creates an instance of the given Transport and configures it using the      * properties set on the given remote broker URI.      *      * @param remoteURI      *        The URI used to connect to a remote Peer.      *      * @return a new Transport instance.      *      * @throws Exception if an error occurs while creating the Transport instance.      */
specifier|public
specifier|static
name|NettyTransport
name|createTransport
parameter_list|(
name|URI
name|remoteURI
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|PropertyUtil
operator|.
name|parseQuery
argument_list|(
name|remoteURI
operator|.
name|getQuery
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|transportURIOptions
init|=
name|PropertyUtil
operator|.
name|filterProperties
argument_list|(
name|map
argument_list|,
literal|"transport."
argument_list|)
decl_stmt|;
name|NettyTransportOptions
name|transportOptions
init|=
literal|null
decl_stmt|;
name|remoteURI
operator|=
name|PropertyUtil
operator|.
name|replaceQuery
argument_list|(
name|remoteURI
argument_list|,
name|map
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|remoteURI
operator|.
name|getScheme
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ssl"
argument_list|)
condition|)
block|{
name|transportOptions
operator|=
name|NettyTransportOptions
operator|.
name|INSTANCE
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|transportOptions
operator|=
name|NettyTransportSslOptions
operator|.
name|INSTANCE
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|unused
init|=
name|PropertyUtil
operator|.
name|setProperties
argument_list|(
name|transportOptions
argument_list|,
name|transportURIOptions
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|unused
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|" Not all transport options could be set on the TCP based"
operator|+
literal|" Transport. Check the options are spelled correctly."
operator|+
literal|" Unused parameters=["
operator|+
name|unused
operator|+
literal|"]."
operator|+
literal|" This provider instance cannot be started."
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|NettyTransport
name|result
init|=
operator|new
name|NettyTransport
argument_list|(
name|remoteURI
argument_list|,
name|transportOptions
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

