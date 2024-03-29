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
name|tool
operator|.
name|sampler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|tool
operator|.
name|ClientRunBasis
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
name|tool
operator|.
name|reports
operator|.
name|PerformanceReportWriter
import|;
end_import

begin_interface
specifier|public
interface|interface
name|PerformanceSampler
extends|extends
name|Runnable
block|{
name|Long
name|getRampUpTime
parameter_list|()
function_decl|;
name|void
name|setRampUpTime
parameter_list|(
name|long
name|rampUpTime
parameter_list|)
function_decl|;
name|Long
name|getRampDownTime
parameter_list|()
function_decl|;
name|void
name|setRampDownTime
parameter_list|(
name|long
name|rampDownTime
parameter_list|)
function_decl|;
name|Long
name|getDuration
parameter_list|()
function_decl|;
name|void
name|setDuration
parameter_list|(
name|long
name|duration
parameter_list|)
function_decl|;
name|long
name|getInterval
parameter_list|()
function_decl|;
name|void
name|setInterval
parameter_list|(
name|long
name|interval
parameter_list|)
function_decl|;
name|long
name|getRampUpPercent
parameter_list|()
function_decl|;
name|void
name|setRampUpPercent
parameter_list|(
name|long
name|rampUpPercent
parameter_list|)
function_decl|;
name|long
name|getRampDownPercent
parameter_list|()
function_decl|;
name|void
name|setRampDownPercent
parameter_list|(
name|long
name|rampDownPercent
parameter_list|)
function_decl|;
name|PerformanceReportWriter
name|getPerfReportWriter
parameter_list|()
function_decl|;
name|void
name|setPerfReportWriter
parameter_list|(
name|PerformanceReportWriter
name|writer
parameter_list|)
function_decl|;
name|PerformanceEventListener
name|getPerfEventListener
parameter_list|()
function_decl|;
name|void
name|setPerfEventListener
parameter_list|(
name|PerformanceEventListener
name|listener
parameter_list|)
function_decl|;
name|void
name|finishSampling
parameter_list|()
function_decl|;
name|void
name|sampleData
parameter_list|()
function_decl|;
name|void
name|startSampler
parameter_list|(
name|CountDownLatch
name|completionLatch
parameter_list|,
name|ClientRunBasis
name|clientRunBasis
parameter_list|,
name|long
name|clientRunDuration
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

