begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|xnet
package|;
end_package

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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|ServiceLogger
implements|implements
name|ServerService
block|{
specifier|private
specifier|final
name|Log
name|log
decl_stmt|;
specifier|private
specifier|final
name|ServerService
name|next
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|logOnSuccess
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|logOnFailure
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|ServiceLogger
parameter_list|(
name|String
name|name
parameter_list|,
name|ServerService
name|next
parameter_list|,
name|String
index|[]
name|logOnSuccess
parameter_list|,
name|String
index|[]
name|logOnFailure
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"OpenEJB.server.service."
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
name|this
operator|.
name|logOnSuccess
operator|=
name|logOnSuccess
expr_stmt|;
name|this
operator|.
name|logOnFailure
operator|=
name|logOnFailure
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * log_on_success      * -----------------      * Different information can be logged when a server starts:      *<p/>      * PID : the server's PID (if it's an internal xinetd service, the PID has then a value of 0) ;      * HOST : the client address ;      * USERID : the identity of the remote user, according to RFC1413 defining identification protocol;      * EXIT : the process exit status;      * DURATION : the session duration.      *<p/>      * log_on_failure      * ------------------      * Here again, xinetd can log a lot of information when a server can't start, either by lack of resources or because of access rules:      * HOST, USERID : like above mentioned ;      * ATTEMPT : logs an access attempt. This an automatic option as soon as another value is provided;      * RECORD : logs every information available on the client.      *      * @param socket      * @throws org.activeio.xnet.ServiceException      *      * @throws IOException      */
specifier|public
name|void
name|service
parameter_list|(
name|Socket
name|socket
parameter_list|)
throws|throws
name|ServiceException
throws|,
name|IOException
block|{
comment|// Fill this in more deeply later.
name|InetAddress
name|client
init|=
name|socket
operator|.
name|getInetAddress
argument_list|()
decl_stmt|;
comment|//        MDC.put("HOST", client.getHostName());
comment|//        MDC.put("SERVER", getName());
try|try
block|{
name|logIncoming
argument_list|()
expr_stmt|;
name|next
operator|.
name|service
argument_list|(
name|socket
argument_list|)
expr_stmt|;
name|logSuccess
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
index|[]
name|getLogOnSuccess
parameter_list|()
block|{
return|return
name|logOnSuccess
return|;
block|}
specifier|public
name|String
index|[]
name|getLogOnFailure
parameter_list|()
block|{
return|return
name|logOnFailure
return|;
block|}
specifier|private
name|void
name|logIncoming
parameter_list|()
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"incomming request"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|logSuccess
parameter_list|()
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"successful request"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|logFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|ServiceException
block|{
name|next
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
name|ServiceException
block|{
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getIP
parameter_list|()
block|{
return|return
name|next
operator|.
name|getIP
argument_list|()
return|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|next
operator|.
name|getPort
argument_list|()
return|;
block|}
block|}
end_class

end_unit

