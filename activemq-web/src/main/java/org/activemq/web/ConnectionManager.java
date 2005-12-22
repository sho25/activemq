begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|web
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSessionEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSessionListener
import|;
end_import

begin_comment
comment|/**  * Listens to sessions closing to ensure that JMS connections are  * cleaned up nicely  *  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionManager
implements|implements
name|HttpSessionListener
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
name|ConnectionManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|sessionCreated
parameter_list|(
name|HttpSessionEvent
name|event
parameter_list|)
block|{     }
specifier|public
name|void
name|sessionDestroyed
parameter_list|(
name|HttpSessionEvent
name|event
parameter_list|)
block|{
comment|/** TODO we can't use the session any more now!          WebClient client = WebClient.getWebClient(event.getSession());          try {          client.stop();          }          catch (JMSException e) {          log.warn("Error closing connection: " + e, e);          }          */
block|}
block|}
end_class

end_unit

