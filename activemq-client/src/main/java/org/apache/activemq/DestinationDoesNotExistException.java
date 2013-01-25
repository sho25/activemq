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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * An exception thrown on a send if a destination does not exist.  * Allows a network bridge to easily cherry-pick and ignore  *   */
end_comment

begin_class
specifier|public
class|class
name|DestinationDoesNotExistException
extends|extends
name|JMSException
block|{
specifier|public
name|DestinationDoesNotExistException
parameter_list|(
name|String
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTemporary
parameter_list|()
block|{
return|return
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"temp-"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalizedMessage
parameter_list|()
block|{
return|return
literal|"The destination "
operator|+
name|getMessage
argument_list|()
operator|+
literal|" does not exist."
return|;
block|}
block|}
end_class

end_unit

