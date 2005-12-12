begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *   * Copyright 2005 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|proxy
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
name|activemq
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
class|class
name|ProxyConnection
implements|implements
name|Service
block|{
specifier|static
specifier|final
specifier|private
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ProxyConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Transport
name|localTransport
decl_stmt|;
specifier|private
specifier|final
name|Transport
name|remoteTransport
decl_stmt|;
specifier|private
name|AtomicBoolean
name|shuttingDown
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|ProxyConnection
parameter_list|(
name|Transport
name|localTransport
parameter_list|,
name|Transport
name|remoteTransport
parameter_list|)
block|{
name|this
operator|.
name|localTransport
operator|=
name|localTransport
expr_stmt|;
name|this
operator|.
name|remoteTransport
operator|=
name|remoteTransport
expr_stmt|;
block|}
specifier|public
name|void
name|onFailure
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shuttingDown
operator|.
name|get
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Transport error: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|running
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|localTransport
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
name|boolean
name|shutdown
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|ShutdownInfo
operator|.
name|class
condition|)
block|{
name|shuttingDown
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|shutdown
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|remoteTransport
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
if|if
condition|(
name|shutdown
condition|)
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|onFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|onFailure
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|error
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|onFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|remoteTransport
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
try|try
block|{
name|localTransport
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|onFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|onFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|localTransport
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteTransport
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|running
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
name|shuttingDown
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|localTransport
argument_list|)
expr_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|remoteTransport
argument_list|)
expr_stmt|;
name|ss
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

