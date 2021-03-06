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
name|URI
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A useful base class for transport implementations.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TransportSupport
extends|extends
name|ServiceSupport
implements|implements
name|Transport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TransportSupport
operator|.
name|class
argument_list|)
decl_stmt|;
name|TransportListener
name|transportListener
decl_stmt|;
comment|/**      * Returns the current transport listener      */
specifier|public
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
block|}
comment|/**      * Registers an inbound command listener      *       * @param commandListener      */
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|commandListener
parameter_list|)
block|{
name|this
operator|.
name|transportListener
operator|=
name|commandListener
expr_stmt|;
block|}
comment|/**      * narrow acceptance      *       * @param target      * @return 'this' if assignable      */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|narrow
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|target
parameter_list|)
block|{
name|boolean
name|assignableFrom
init|=
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|assignableFrom
condition|)
block|{
return|return
name|target
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Object
name|command
parameter_list|,
name|ResponseCallback
name|responseCallback
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
comment|/**      * Process the inbound command      */
specifier|public
name|void
name|doConsume
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No transportListener available to process inbound command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Passes any IO exceptions into the transport listener      */
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|transportListener
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e2
parameter_list|)
block|{
comment|// Handle any unexpected runtime exceptions by debug logging
comment|// them.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unexpected runtime exception: "
operator|+
name|e2
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|checkStarted
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isStarted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The transport is not running."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|reconnect
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isReconnectSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isUpdateURIsSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|updateURIs
parameter_list|(
name|boolean
name|reblance
parameter_list|,
name|URI
index|[]
name|uris
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isDisposed
parameter_list|()
block|{
return|return
name|isStopped
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|isStarted
argument_list|()
return|;
block|}
block|}
end_class

end_unit

