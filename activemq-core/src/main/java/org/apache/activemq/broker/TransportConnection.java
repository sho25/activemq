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
name|broker
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|ft
operator|.
name|MasterBroker
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
name|BrokerInfo
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|TransportListener
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

begin_comment
comment|/**  *   * @version $Revision: 1.8 $  */
end_comment

begin_class
specifier|public
class|class
name|TransportConnection
extends|extends
name|AbstractConnection
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
name|TransportConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Transport
name|transport
decl_stmt|;
specifier|private
name|boolean
name|slow
decl_stmt|;
specifier|private
name|boolean
name|markedCandidate
decl_stmt|;
specifier|private
name|boolean
name|blockedCandidate
decl_stmt|;
specifier|private
name|boolean
name|blocked
decl_stmt|;
specifier|private
name|boolean
name|connected
decl_stmt|;
specifier|private
name|boolean
name|active
decl_stmt|;
specifier|private
name|long
name|timeStamp
init|=
literal|0
decl_stmt|;
specifier|private
name|MasterBroker
name|masterBroker
decl_stmt|;
comment|//used if this connection is used by a Slave
comment|/**      * @param connector      * @param transport      * @param broker      * @param taskRunnerFactory - can be null if you want direct dispatch to the transport else commands are sent async.      */
specifier|public
name|TransportConnection
parameter_list|(
name|TransportConnector
name|connector
parameter_list|,
specifier|final
name|Transport
name|transport
parameter_list|,
name|Broker
name|broker
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|)
block|{
name|super
argument_list|(
name|connector
argument_list|,
name|broker
argument_list|,
name|taskRunnerFactory
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setBrokerName
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|transport
operator|.
name|setTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|Response
name|response
init|=
name|service
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|dispatch
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
name|serviceTransportException
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connected
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
name|active
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|connector
operator|.
name|onStarted
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|connector
operator|.
name|onStopped
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|masterBroker
operator|!=
literal|null
condition|)
block|{
name|masterBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|transport
operator|.
name|oneway
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{
comment|//ignore.printStackTrace();
block|}
name|transport
operator|.
name|stop
argument_list|()
expr_stmt|;
name|active
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Returns the blockedCandidate.      */
specifier|public
name|boolean
name|isBlockedCandidate
parameter_list|()
block|{
return|return
name|blockedCandidate
return|;
block|}
comment|/**      * @param blockedCandidate      *            The blockedCandidate to set.      */
specifier|public
name|void
name|setBlockedCandidate
parameter_list|(
name|boolean
name|blockedCandidate
parameter_list|)
block|{
name|this
operator|.
name|blockedCandidate
operator|=
name|blockedCandidate
expr_stmt|;
block|}
comment|/**      * @return Returns the markedCandidate.      */
specifier|public
name|boolean
name|isMarkedCandidate
parameter_list|()
block|{
return|return
name|markedCandidate
return|;
block|}
comment|/**      * @param markedCandidate      *            The markedCandidate to set.      */
specifier|public
name|void
name|setMarkedCandidate
parameter_list|(
name|boolean
name|markedCandidate
parameter_list|)
block|{
name|this
operator|.
name|markedCandidate
operator|=
name|markedCandidate
expr_stmt|;
if|if
condition|(
operator|!
name|markedCandidate
condition|)
block|{
name|timeStamp
operator|=
literal|0
expr_stmt|;
name|blockedCandidate
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      * @param slow      *            The slow to set.      */
specifier|public
name|void
name|setSlow
parameter_list|(
name|boolean
name|slow
parameter_list|)
block|{
name|this
operator|.
name|slow
operator|=
name|slow
expr_stmt|;
block|}
comment|/**      * @return true if the Connection is slow      */
specifier|public
name|boolean
name|isSlow
parameter_list|()
block|{
return|return
name|slow
return|;
block|}
comment|/**      * @return true if the Connection is potentially blocked      */
specifier|public
name|boolean
name|isMarkedBlockedCandidate
parameter_list|()
block|{
return|return
name|markedCandidate
return|;
block|}
comment|/**      * Mark the Connection, so we can deem if it's collectable on the next sweep      */
specifier|public
name|void
name|doMark
parameter_list|()
block|{
if|if
condition|(
name|timeStamp
operator|==
literal|0
condition|)
block|{
name|timeStamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return if after being marked, the Connection is still writing      */
specifier|public
name|boolean
name|isBlocked
parameter_list|()
block|{
return|return
name|blocked
return|;
block|}
comment|/**      * @return true if the Connection is connected      */
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|connected
return|;
block|}
comment|/**      * @param blocked      *            The blocked to set.      */
specifier|public
name|void
name|setBlocked
parameter_list|(
name|boolean
name|blocked
parameter_list|)
block|{
name|this
operator|.
name|blocked
operator|=
name|blocked
expr_stmt|;
block|}
comment|/**      * @param connected      *            The connected to set.      */
specifier|public
name|void
name|setConnected
parameter_list|(
name|boolean
name|connected
parameter_list|)
block|{
name|this
operator|.
name|connected
operator|=
name|connected
expr_stmt|;
block|}
comment|/**      * @return true if the Connection is active      */
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|active
return|;
block|}
comment|/**      * @param active      *            The active to set.      */
specifier|public
name|void
name|setActive
parameter_list|(
name|boolean
name|active
parameter_list|)
block|{
name|this
operator|.
name|active
operator|=
name|active
expr_stmt|;
block|}
specifier|public
name|Response
name|processBrokerInfo
parameter_list|(
name|BrokerInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|isSlaveBroker
argument_list|()
condition|)
block|{
comment|//stream messages from this broker (the master) to
comment|//the slave
name|MutableBrokerFilter
name|parent
init|=
operator|(
name|MutableBrokerFilter
operator|)
name|broker
operator|.
name|getAdaptor
argument_list|(
name|MutableBrokerFilter
operator|.
name|class
argument_list|)
decl_stmt|;
name|masterBroker
operator|=
operator|new
name|MasterBroker
argument_list|(
name|parent
argument_list|,
name|transport
argument_list|)
expr_stmt|;
name|masterBroker
operator|.
name|startProcessing
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Slave Broker "
operator|+
name|info
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|" is attached"
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|processBrokerInfo
argument_list|(
name|info
argument_list|)
return|;
block|}
specifier|protected
name|void
name|dispatch
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
name|setMarkedCandidate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|getStatistics
argument_list|()
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|serviceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setMarkedCandidate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

