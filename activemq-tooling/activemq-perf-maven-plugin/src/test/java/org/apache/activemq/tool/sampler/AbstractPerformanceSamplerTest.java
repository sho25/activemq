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
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|AbstractPerformanceSamplerTest
block|{
specifier|private
class|class
name|EmptySampler
extends|extends
name|AbstractPerformanceSampler
block|{
annotation|@
name|Override
specifier|public
name|void
name|sampleData
parameter_list|()
block|{}
block|}
specifier|private
name|AbstractPerformanceSampler
name|sampler
decl_stmt|;
specifier|private
name|CountDownLatch
name|samplerLatch
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUpSampler
parameter_list|()
block|{
name|sampler
operator|=
operator|new
name|EmptySampler
argument_list|()
expr_stmt|;
name|samplerLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetRampUpPercent_exceeds100
parameter_list|()
block|{
name|sampler
operator|.
name|setRampUpPercent
argument_list|(
literal|101
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetRampUpPercent_lessThan0
parameter_list|()
block|{
name|sampler
operator|.
name|setRampUpPercent
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetRampDownPercent_exceeds99
parameter_list|()
block|{
name|sampler
operator|.
name|setRampDownPercent
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetRampDownPercent_lessThan0
parameter_list|()
block|{
name|sampler
operator|.
name|setRampDownPercent
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSamplerOnCountBasis
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
name|samplerLatch
decl_stmt|;
name|sampler
operator|.
name|startSampler
argument_list|(
name|latch
argument_list|,
name|ClientRunBasis
operator|.
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|finishSampling
argument_list|()
expr_stmt|;
name|samplerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|sampler
operator|.
name|getDuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampUpTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampDownTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSamplerOnTimeBasis_matchesClientSettings
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
name|samplerLatch
decl_stmt|;
name|sampler
operator|.
name|startSampler
argument_list|(
name|latch
argument_list|,
name|ClientRunBasis
operator|.
name|time
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|samplerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getDuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampUpTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampDownTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSamplerOnTimeBasis_percentageOverrides
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
name|samplerLatch
decl_stmt|;
name|sampler
operator|.
name|setRampUpPercent
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|setRampDownPercent
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|startSampler
argument_list|(
name|latch
argument_list|,
name|ClientRunBasis
operator|.
name|time
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|samplerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getDuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampUpTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampDownTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSamplerOnTimeBasis_percentageOverridesExceedSamplerDuration
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
name|samplerLatch
decl_stmt|;
name|sampler
operator|.
name|setRampUpPercent
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|setRampDownPercent
argument_list|(
literal|41
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|startSampler
argument_list|(
name|latch
argument_list|,
name|ClientRunBasis
operator|.
name|time
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSamplerOnTimeBasis_timeOverrides
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
name|samplerLatch
decl_stmt|;
name|sampler
operator|.
name|setRampUpTime
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|setRampDownTime
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|sampler
operator|.
name|startSampler
argument_list|(
name|latch
argument_list|,
name|ClientRunBasis
operator|.
name|time
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|samplerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getDuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampUpTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
operator|(
name|long
operator|)
name|sampler
operator|.
name|getRampDownTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

