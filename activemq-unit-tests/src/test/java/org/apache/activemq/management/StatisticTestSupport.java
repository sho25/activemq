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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|StatisticTestSupport
extends|extends
name|TestCase
block|{
comment|/**      * assert method used by the management related classes for its usecase.      *       * @param counter      * @param name      * @param unit      * @param description      */
specifier|protected
name|void
name|assertStatistic
parameter_list|(
name|StatisticImpl
name|counter
parameter_list|,
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
name|assertEquals
argument_list|(
name|name
argument_list|,
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unit
argument_list|,
name|counter
operator|.
name|getUnit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|description
argument_list|,
name|counter
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * assert method to determine last time vs the start time.      *       * @param counter      */
specifier|protected
name|void
name|assertLastTimeNotStartTime
parameter_list|(
name|StatisticImpl
name|counter
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should not have start time the same as last sample time. Start time: "
operator|+
name|counter
operator|.
name|getStartTime
argument_list|()
operator|+
literal|" lastTime: "
operator|+
name|counter
operator|.
name|getLastSampleTime
argument_list|()
argument_list|,
name|counter
operator|.
name|getStartTime
argument_list|()
operator|!=
name|counter
operator|.
name|getLastSampleTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

