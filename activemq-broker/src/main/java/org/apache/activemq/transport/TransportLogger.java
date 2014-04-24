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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * This TransportFilter implementation writes output to a log  * as it intercepts commands / events before sending them to the  * following layer in the Transport stack.  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  *   */
end_comment

begin_class
specifier|public
class|class
name|TransportLogger
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|final
name|Logger
name|log
decl_stmt|;
specifier|private
name|boolean
name|logging
decl_stmt|;
specifier|private
specifier|final
name|LogWriter
name|logWriter
decl_stmt|;
specifier|private
name|TransportLoggerView
name|view
decl_stmt|;
specifier|public
name|TransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|,
name|Logger
name|log
parameter_list|,
name|boolean
name|startLogging
parameter_list|,
name|LogWriter
name|logWriter
parameter_list|)
block|{
comment|// Changed constructor to pass the implementation of the LogWriter interface
comment|// that will be used to write the messages.
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
name|this
operator|.
name|logging
operator|=
name|startLogging
expr_stmt|;
name|this
operator|.
name|logWriter
operator|=
name|logWriter
expr_stmt|;
block|}
comment|/**      * Returns true if logging is activated for this TransportLogger, false otherwise.      * @return true if logging is activated for this TransportLogger, false otherwise.      */
specifier|public
name|boolean
name|isLogging
parameter_list|()
block|{
return|return
name|logging
return|;
block|}
comment|/**      * Sets if logging should be activated for this TransportLogger.      * @param logging true to activate logging, false to deactivate.      */
specifier|public
name|void
name|setLogging
parameter_list|(
name|boolean
name|logging
parameter_list|)
block|{
name|this
operator|.
name|logging
operator|=
name|logging
expr_stmt|;
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
comment|// Changed this method to use a LogWriter object to actually
comment|// print the messages to the log, and only in case of logging
comment|// being active, instead of logging the message directly.
if|if
condition|(
name|logging
condition|)
name|logWriter
operator|.
name|logRequest
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
name|Object
name|rc
init|=
name|super
operator|.
name|request
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|logging
condition|)
name|logWriter
operator|.
name|logResponse
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
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
comment|// Changed this method to use a LogWriter object to actually
comment|// print the messages to the log, and only in case of logging
comment|// being active, instead of logging the message directly.
if|if
condition|(
name|logging
condition|)
name|logWriter
operator|.
name|logRequest
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
name|Object
name|rc
init|=
name|super
operator|.
name|request
argument_list|(
name|command
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|logging
condition|)
name|logWriter
operator|.
name|logResponse
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
return|return
name|rc
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
comment|// Changed this method to use a LogWriter object to actually
comment|// print the messages to the log, and only in case of logging
comment|// being active, instead of logging the message directly.
if|if
condition|(
name|logging
condition|)
name|logWriter
operator|.
name|logAsyncRequest
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
name|FutureResponse
name|rc
init|=
name|next
operator|.
name|asyncRequest
argument_list|(
name|command
argument_list|,
name|responseCallback
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Changed this method to use a LogWriter object to actually
comment|// print the messages to the log, and only in case of logging
comment|// being active, instead of logging the message directly.
if|if
condition|(
name|logging
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logWriter
operator|.
name|logOneWay
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
name|next
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
comment|// Changed this method to use a LogWriter object to actually
comment|// print the messages to the log, and only in case of logging
comment|// being active, instead of logging the message directly.
if|if
condition|(
name|logging
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logWriter
operator|.
name|logReceivedCommand
argument_list|(
name|log
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
name|getTransportListener
argument_list|()
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
comment|// Changed this method to use a LogWriter object to actually
comment|// print the messages to the log, and only in case of logging
comment|// being active, instead of logging the message directly.
if|if
condition|(
name|logging
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logWriter
operator|.
name|logReceivedException
argument_list|(
name|log
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the associated MBean for this TransportLogger.      * @return the associated MBean for this TransportLogger.      */
specifier|public
name|TransportLoggerView
name|getView
parameter_list|()
block|{
return|return
name|view
return|;
block|}
comment|/**      * Sets the associated MBean for this TransportLogger.      * @param view the associated MBean for this TransportLogger.      */
specifier|public
name|void
name|setView
parameter_list|(
name|TransportLoggerView
name|view
parameter_list|)
block|{
name|this
operator|.
name|view
operator|=
name|view
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|next
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|view
operator|!=
literal|null
condition|)
block|{
name|view
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

