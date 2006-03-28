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
operator|.
name|http
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
name|transport
operator|.
name|TransportAcceptListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
comment|/**  * This servlet embeds an ActiveMQ broker inside a servlet engine which is ideal  * for deploying ActiveMQ inside a WAR and using this servlet as a HTTP tunnel.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|HttpEmbeddedTunnelServlet
extends|extends
name|HttpTunnelServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3705734740251302361L
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|HttpTransportServer
name|transportConnector
decl_stmt|;
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
comment|// lets initialize the ActiveMQ broker
try|try
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
comment|// Add the servlet connector
name|String
name|url
init|=
name|getConnectorURL
argument_list|()
decl_stmt|;
name|transportConnector
operator|=
operator|new
name|HttpTransportServer
argument_list|(
operator|new
name|URI
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|transportConnector
argument_list|)
expr_stmt|;
name|String
name|brokerURL
init|=
name|getServletContext
argument_list|()
operator|.
name|getInitParameter
argument_list|(
literal|"org.apache.activemq.brokerURL"
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerURL
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Listening for internal communication on: "
operator|+
name|brokerURL
argument_list|)
expr_stmt|;
block|}
block|}
name|broker
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to start embedded broker: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// now lets register the listener
name|TransportAcceptListener
name|listener
init|=
name|transportConnector
operator|.
name|getAcceptListener
argument_list|()
decl_stmt|;
name|getServletContext
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"transportChannelListener"
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**      * Factory method to create a new broker      *       * @throws Exception      */
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|String
name|getConnectorURL
parameter_list|()
block|{
return|return
literal|"http://localhost/"
operator|+
name|getServletContext
argument_list|()
operator|.
name|getServletContextName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

