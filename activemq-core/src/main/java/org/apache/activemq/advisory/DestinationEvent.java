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
operator|.
name|advisory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventObject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|ConsumerId
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
name|DestinationInfo
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
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * An event caused when a destination is created or deleted  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DestinationEvent
extends|extends
name|EventObject
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2442156576867593780L
decl_stmt|;
specifier|private
name|DestinationInfo
name|destinationInfo
decl_stmt|;
specifier|public
name|DestinationEvent
parameter_list|(
name|DestinationSource
name|source
parameter_list|,
name|DestinationInfo
name|destinationInfo
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|destinationInfo
operator|=
name|destinationInfo
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|getDestinationInfo
argument_list|()
operator|.
name|getDestination
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isAddOperation
parameter_list|()
block|{
return|return
name|getDestinationInfo
argument_list|()
operator|.
name|isAddOperation
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|getDestinationInfo
argument_list|()
operator|.
name|getTimeout
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isRemoveOperation
parameter_list|()
block|{
return|return
name|getDestinationInfo
argument_list|()
operator|.
name|isRemoveOperation
argument_list|()
return|;
block|}
specifier|public
name|DestinationInfo
name|getDestinationInfo
parameter_list|()
block|{
return|return
name|destinationInfo
return|;
block|}
block|}
end_class

end_unit

