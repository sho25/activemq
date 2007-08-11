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
name|management
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|j2ee
operator|.
name|statistics
operator|.
name|Statistic
import|;
end_import

begin_comment
comment|/**  * Base class for a Statistic implementation  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|StatisticImpl
implements|implements
name|Statistic
implements|,
name|Resettable
block|{
specifier|protected
name|boolean
name|enabled
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|unit
decl_stmt|;
specifier|private
name|String
name|description
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
name|long
name|lastSampleTime
decl_stmt|;
specifier|public
name|StatisticImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|unit
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|lastSampleTime
operator|=
name|startTime
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|lastSampleTime
operator|=
name|startTime
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|void
name|updateSampleTime
parameter_list|()
block|{
name|lastSampleTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|appendFieldDescription
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|String
name|getUnit
parameter_list|()
block|{
return|return
name|unit
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
specifier|public
specifier|synchronized
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
specifier|public
specifier|synchronized
name|long
name|getLastSampleTime
parameter_list|()
block|{
return|return
name|lastSampleTime
return|;
block|}
comment|/**      * @return the enabled      */
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabled
return|;
block|}
comment|/**      * @param enabled the enabled to set      */
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|void
name|appendFieldDescription
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" unit: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|unit
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" startTime: "
argument_list|)
expr_stmt|;
comment|// buffer.append(new Date(startTime));
name|buffer
operator|.
name|append
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" lastSampleTime: "
argument_list|)
expr_stmt|;
comment|// buffer.append(new Date(lastSampleTime));
name|buffer
operator|.
name|append
argument_list|(
name|lastSampleTime
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" description: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

