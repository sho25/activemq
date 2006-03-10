begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|command
operator|.
name|Command
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
name|command
operator|.
name|RemoveInfo
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
name|command
operator|.
name|Response
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
name|command
operator|.
name|ShutdownInfo
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A useful base class for transport implementations.  *   * @version $Revision: 1.1 $  */
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
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
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
name|Object
name|narrow
parameter_list|(
name|Class
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
name|this
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
name|Command
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
name|Response
name|request
parameter_list|(
name|Command
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
name|Response
name|request
parameter_list|(
name|Command
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
name|Command
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
name|log
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
name|transportListener
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|checkStarted
parameter_list|(
name|Command
name|command
parameter_list|)
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
comment|// we might try to shut down the transport before it was ever started in some test cases
if|if
condition|(
operator|!
operator|(
name|command
operator|instanceof
name|ShutdownInfo
operator|||
name|command
operator|instanceof
name|RemoveInfo
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The transport "
operator|+
name|this
operator|+
literal|" of type: "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is not running."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

