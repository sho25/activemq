begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2009 Red Hat, Inc.  * Red Hat licenses this file to you under the Apache License, version  * 2.0 (the "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *    http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  * implied.  See the License for the specific language governing  * permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|example
operator|.
name|server
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|ActivationConfigProperty
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|MessageDriven
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|MessageDrivenContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|TransactionAttribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|TransactionAttributeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|TransactionManagement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|TransactionManagementType
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
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|ejb3
operator|.
name|annotation
operator|.
name|ResourceAdapter
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>  */
end_comment

begin_class
annotation|@
name|MessageDriven
argument_list|(
name|name
operator|=
literal|"MDB_CMT_SetRollbackOnlyExample"
argument_list|,
name|activationConfig
operator|=
block|{
annotation|@
name|ActivationConfigProperty
argument_list|(
name|propertyName
operator|=
literal|"destinationType"
argument_list|,
name|propertyValue
operator|=
literal|"javax.jms.Queue"
argument_list|)
block|,
annotation|@
name|ActivationConfigProperty
argument_list|(
name|propertyName
operator|=
literal|"destination"
argument_list|,
name|propertyValue
operator|=
literal|"queue/testQueue"
argument_list|)
block|}
argument_list|)
annotation|@
name|TransactionManagement
argument_list|(
name|value
operator|=
name|TransactionManagementType
operator|.
name|CONTAINER
argument_list|)
annotation|@
name|TransactionAttribute
argument_list|(
name|value
operator|=
name|TransactionAttributeType
operator|.
name|REQUIRED
argument_list|)
annotation|@
name|ResourceAdapter
argument_list|(
literal|"activemq-rar.rar"
argument_list|)
specifier|public
class|class
name|MDB_CMT_SetRollbackOnlyExample
implements|implements
name|MessageListener
block|{
annotation|@
name|Resource
name|MessageDrivenContext
name|ctx
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
specifier|final
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
comment|// Step 9. We know the client is sending a text message so we cast
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
comment|// Step 10. get the text from the message.
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|textMessage
operator|.
name|getJMSRedelivered
argument_list|()
condition|)
block|{
comment|// Step 11. rollback delivery of message if the first time
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"message "
operator|+
name|text
operator|+
literal|" received for the first time"
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|setRollbackOnly
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Step 12. read the message
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"message "
operator|+
name|text
operator|+
literal|" received for the second time"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

