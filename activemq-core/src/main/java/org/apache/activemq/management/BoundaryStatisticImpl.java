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

begin_comment
comment|/**  * A boundary statistic implementation  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|BoundaryStatisticImpl
extends|extends
name|StatisticImpl
block|{
specifier|private
name|long
name|lowerBound
decl_stmt|;
specifier|private
name|long
name|upperBound
decl_stmt|;
specifier|public
name|BoundaryStatisticImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|unit
parameter_list|,
name|String
name|description
parameter_list|,
name|long
name|lowerBound
parameter_list|,
name|long
name|upperBound
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|unit
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowerBound
operator|=
name|lowerBound
expr_stmt|;
name|this
operator|.
name|upperBound
operator|=
name|upperBound
expr_stmt|;
block|}
specifier|public
name|long
name|getLowerBound
parameter_list|()
block|{
return|return
name|lowerBound
return|;
block|}
specifier|public
name|long
name|getUpperBound
parameter_list|()
block|{
return|return
name|upperBound
return|;
block|}
specifier|protected
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
literal|" lowerBound: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|lowerBound
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" upperBound: "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|upperBound
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|appendFieldDescription
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

