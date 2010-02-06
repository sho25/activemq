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
name|web
operator|.
name|controller
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|HttpServletResponse
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
name|broker
operator|.
name|jmx
operator|.
name|JobSchedulerViewMBean
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
name|web
operator|.
name|BrokerFacade
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
name|web
operator|.
name|DestinationFacade
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
name|springframework
operator|.
name|web
operator|.
name|servlet
operator|.
name|ModelAndView
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|servlet
operator|.
name|mvc
operator|.
name|Controller
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 700405 $  */
end_comment

begin_class
specifier|public
class|class
name|DeleteJob
extends|extends
name|DestinationFacade
implements|implements
name|Controller
block|{
specifier|private
name|String
name|jobId
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DeleteJob
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|DeleteJob
parameter_list|(
name|BrokerFacade
name|brokerFacade
parameter_list|)
block|{
name|super
argument_list|(
name|brokerFacade
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ModelAndView
name|handleRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|jobId
operator|!=
literal|null
condition|)
block|{
name|JobSchedulerViewMBean
name|jobScheduler
init|=
name|getBrokerFacade
argument_list|()
operator|.
name|getJobScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|jobScheduler
operator|!=
literal|null
condition|)
block|{
name|jobScheduler
operator|.
name|removeJob
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed scheduled Job "
operator|+
name|jobId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Scheduler not configured"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ModelAndView
argument_list|(
literal|"redirect:scheduled.jsp"
argument_list|)
return|;
block|}
specifier|public
name|String
name|getJobId
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
specifier|public
name|void
name|setJobId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|id
expr_stmt|;
block|}
block|}
end_class

end_unit

