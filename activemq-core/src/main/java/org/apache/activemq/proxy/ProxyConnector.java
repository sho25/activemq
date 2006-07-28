begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
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
name|Iterator
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
name|transport
operator|.
name|CompositeTransport
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
name|TransportAcceptListener
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
name|TransportFilter
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
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ProxyConnector
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
name|ProxyConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TransportServer
name|server
decl_stmt|;
specifier|private
name|URI
name|bind
decl_stmt|;
specifier|private
name|URI
name|remote
decl_stmt|;
specifier|private
name|URI
name|localUri
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
name|CopyOnWriteArrayList
name|connections
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|getServer
argument_list|()
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|TransportAcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Transport
name|localTransport
parameter_list|)
block|{
try|try
block|{
name|Transport
name|remoteTransport
init|=
name|createRemoteTransport
argument_list|()
decl_stmt|;
name|ProxyConnection
name|connection
init|=
operator|new
name|ProxyConnection
argument_list|(
name|localTransport
argument_list|,
name|remoteTransport
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not accept connection: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|getServer
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Proxy Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Started"
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
name|ServiceStopper
name|ss
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|server
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|stop
argument_list|(
name|this
operator|.
name|server
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|connections
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Connector stopped: Stopping proxy."
argument_list|)
expr_stmt|;
name|ss
operator|.
name|stop
argument_list|(
operator|(
name|Service
operator|)
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Proxy Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Stopped"
argument_list|)
expr_stmt|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|URI
name|getLocalUri
parameter_list|()
block|{
return|return
name|localUri
return|;
block|}
specifier|public
name|void
name|setLocalUri
parameter_list|(
name|URI
name|localURI
parameter_list|)
block|{
name|this
operator|.
name|localUri
operator|=
name|localURI
expr_stmt|;
block|}
specifier|public
name|URI
name|getBind
parameter_list|()
block|{
return|return
name|bind
return|;
block|}
specifier|public
name|void
name|setBind
parameter_list|(
name|URI
name|bind
parameter_list|)
block|{
name|this
operator|.
name|bind
operator|=
name|bind
expr_stmt|;
block|}
specifier|public
name|URI
name|getRemote
parameter_list|()
block|{
return|return
name|remote
return|;
block|}
specifier|public
name|void
name|setRemote
parameter_list|(
name|URI
name|remote
parameter_list|)
block|{
name|this
operator|.
name|remote
operator|=
name|remote
expr_stmt|;
block|}
specifier|public
name|TransportServer
name|getServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
name|createServer
argument_list|()
expr_stmt|;
block|}
return|return
name|server
return|;
block|}
specifier|public
name|void
name|setServer
parameter_list|(
name|TransportServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
specifier|protected
name|TransportServer
name|createServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|bind
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"You must specify either a server or the bind property"
argument_list|)
throw|;
block|}
return|return
name|TransportFactory
operator|.
name|bind
argument_list|(
literal|null
argument_list|,
name|bind
argument_list|)
return|;
block|}
specifier|private
name|Transport
name|createRemoteTransport
parameter_list|()
throws|throws
name|Exception
block|{
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|compositeConnect
argument_list|(
name|remote
argument_list|)
decl_stmt|;
name|CompositeTransport
name|ct
init|=
operator|(
name|CompositeTransport
operator|)
name|transport
operator|.
name|narrow
argument_list|(
name|CompositeTransport
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|ct
operator|!=
literal|null
operator|&&
name|localUri
operator|!=
literal|null
condition|)
block|{
name|ct
operator|.
name|add
argument_list|(
operator|new
name|URI
index|[]
block|{
name|localUri
block|}
argument_list|)
expr_stmt|;
block|}
comment|// Add a transport filter so that can track the transport life cycle
name|transport
operator|=
operator|new
name|TransportFilter
argument_list|(
name|transport
argument_list|)
block|{
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stopping proxy."
argument_list|)
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connections
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
return|return
name|transport
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|server
operator|.
name|getConnectURI
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"proxy"
expr_stmt|;
block|}
block|}
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
end_class

end_unit

