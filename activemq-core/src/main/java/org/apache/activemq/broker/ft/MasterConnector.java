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
operator|.
name|ft
package|;
end_package

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
name|atomic
operator|.
name|AtomicBoolean
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
name|Service
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
name|CommandTypes
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
name|ConnectionId
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
name|ConnectionInfo
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
name|MessageDispatch
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
name|ProducerInfo
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
name|SessionInfo
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
name|TransportListener
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
name|ServiceStopper
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Used by a Slave Broker to Connect to the Master  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MasterConnector
implements|implements
name|Service
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
name|MasterConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|URI
name|remoteURI
decl_stmt|;
specifier|private
name|URI
name|localURI
decl_stmt|;
specifier|private
name|Transport
name|localBroker
decl_stmt|;
specifier|private
name|Transport
name|remoteBroker
decl_stmt|;
specifier|private
name|TransportConnector
name|connector
decl_stmt|;
specifier|private
name|AtomicBoolean
name|masterActive
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|IdGenerator
name|idGenerator
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
name|SessionInfo
name|sessionInfo
decl_stmt|;
name|ProducerInfo
name|producerInfo
decl_stmt|;
specifier|public
name|MasterConnector
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|TransportConnector
name|connector
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|connector
operator|=
name|connector
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSlave
parameter_list|()
block|{
return|return
name|masterActive
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|localBroker
operator|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|localURI
argument_list|)
expr_stmt|;
name|remoteBroker
operator|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|remoteURI
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting a network connection between "
operator|+
name|localBroker
operator|+
literal|" and "
operator|+
name|remoteBroker
operator|+
literal|" has been established."
argument_list|)
expr_stmt|;
name|localBroker
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
block|{             }
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|serviceLocalException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|remoteBroker
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
name|serviceRemoteCommand
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
name|serviceRemoteException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|masterActive
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
name|thead
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|startBridge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|masterActive
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Failed to start network bridge: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thead
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|startBridge
parameter_list|()
throws|throws
name|Exception
block|{
name|connectionInfo
operator|=
operator|new
name|ConnectionInfo
argument_list|()
expr_stmt|;
name|connectionInfo
operator|.
name|setConnectionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
name|idGenerator
operator|.
name|generateId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connectionInfo
operator|.
name|setClientId
argument_list|(
name|idGenerator
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|sessionInfo
operator|=
operator|new
name|SessionInfo
argument_list|(
name|connectionInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|producerInfo
operator|=
operator|new
name|ProducerInfo
argument_list|(
name|sessionInfo
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|producerInfo
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
name|BrokerInfo
name|brokerInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|connector
operator|!=
literal|null
condition|)
block|{
name|brokerInfo
operator|=
name|connector
operator|.
name|getBrokerInfo
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|brokerInfo
operator|=
operator|new
name|BrokerInfo
argument_list|()
expr_stmt|;
block|}
name|brokerInfo
operator|.
name|setBrokerName
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|brokerInfo
operator|.
name|setPeerBrokerInfos
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getPeerBrokerInfos
argument_list|()
argument_list|)
expr_stmt|;
name|brokerInfo
operator|.
name|setSlaveBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|brokerInfo
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Slave connection between "
operator|+
name|localBroker
operator|+
literal|" and "
operator|+
name|remoteBroker
operator|+
literal|" has been established."
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
name|masterActive
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|connectionInfo
operator|!=
literal|null
condition|)
block|{
name|localBroker
operator|.
name|request
argument_list|(
name|connectionInfo
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|localBroker
operator|.
name|setTransportListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setTransportListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|)
expr_stmt|;
name|localBroker
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
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Caught exception stopping"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ServiceStopper
name|ss
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|localBroker
argument_list|)
expr_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|remoteBroker
argument_list|)
expr_stmt|;
name|ss
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|serviceRemoteException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Network connection between "
operator|+
name|localBroker
operator|+
literal|" and "
operator|+
name|remoteBroker
operator|+
literal|" shutdown: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|shutDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|serviceRemoteCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|command
operator|.
name|isMessageDispatch
argument_list|()
condition|)
block|{
name|MessageDispatch
name|md
init|=
operator|(
name|MessageDispatch
operator|)
name|command
decl_stmt|;
name|command
operator|=
name|md
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|CommandTypes
operator|.
name|SHUTDOWN_INFO
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"The Master has shutdown"
argument_list|)
expr_stmt|;
name|shutDown
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|localBroker
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|serviceRemoteException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|serviceLocalException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Network connection between "
operator|+
name|localBroker
operator|+
literal|" and "
operator|+
name|remoteBroker
operator|+
literal|" shutdown: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the localURI.      */
specifier|public
name|URI
name|getLocalURI
parameter_list|()
block|{
return|return
name|localURI
return|;
block|}
comment|/**      * @param localURI      *            The localURI to set.      */
specifier|public
name|void
name|setLocalURI
parameter_list|(
name|URI
name|localURI
parameter_list|)
block|{
name|this
operator|.
name|localURI
operator|=
name|localURI
expr_stmt|;
block|}
comment|/**      * @return Returns the remoteURI.      */
specifier|public
name|URI
name|getRemoteURI
parameter_list|()
block|{
return|return
name|remoteURI
return|;
block|}
comment|/**      * @param remoteURI      *            The remoteURI to set.      */
specifier|public
name|void
name|setRemoteURI
parameter_list|(
name|URI
name|remoteURI
parameter_list|)
block|{
name|this
operator|.
name|remoteURI
operator|=
name|remoteURI
expr_stmt|;
block|}
specifier|private
name|void
name|shutDown
parameter_list|()
block|{
name|masterActive
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|masterFailed
argument_list|()
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

