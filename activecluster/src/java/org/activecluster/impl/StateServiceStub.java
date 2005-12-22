begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activecluster
operator|.
name|impl
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
name|org
operator|.
name|activecluster
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  * A local stub for the state service which sends JMS messages  * to the cluster  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|StateServiceStub
implements|implements
name|StateService
block|{
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|public
name|StateServiceStub
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|producer
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
specifier|public
name|void
name|keepAlive
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Sending cluster data message: "
operator|+
name|node
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
name|session
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|NodeImpl
argument_list|(
name|node
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not send JMS message: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Sending shutdown message: "
operator|+
name|node
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
name|session
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|NodeImpl
argument_list|(
name|node
argument_list|)
argument_list|)
decl_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not send JMS message: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

