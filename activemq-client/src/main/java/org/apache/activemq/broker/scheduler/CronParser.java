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
name|broker
operator|.
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageFormatException
import|;
end_import

begin_class
specifier|public
class|class
name|CronParser
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_TOKENS
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MINUTES
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|HOURS
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DAY_OF_MONTH
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MONTH
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DAY_OF_WEEK
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
name|long
name|getNextScheduledTime
parameter_list|(
specifier|final
name|String
name|cronEntry
parameter_list|,
name|long
name|currentTime
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|long
name|result
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|cronEntry
operator|==
literal|null
operator|||
name|cronEntry
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
comment|// Handle the once per minute case "* * * * *"
comment|// starting the next event at the top of the minute.
if|if
condition|(
name|cronEntry
operator|.
name|equals
argument_list|(
literal|"* * * * *"
argument_list|)
condition|)
block|{
name|result
operator|=
name|currentTime
operator|+
literal|60
operator|*
literal|1000
expr_stmt|;
name|result
operator|=
name|result
operator|/
literal|1000
operator|*
literal|1000
expr_stmt|;
return|return
name|result
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|tokenize
argument_list|(
name|cronEntry
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CronEntry
argument_list|>
name|entries
init|=
name|buildCronEntries
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|Calendar
name|working
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|working
operator|.
name|setTimeInMillis
argument_list|(
name|currentTime
argument_list|)
expr_stmt|;
name|working
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CronEntry
name|minutes
init|=
name|entries
operator|.
name|get
argument_list|(
name|MINUTES
argument_list|)
decl_stmt|;
name|CronEntry
name|hours
init|=
name|entries
operator|.
name|get
argument_list|(
name|HOURS
argument_list|)
decl_stmt|;
name|CronEntry
name|dayOfMonth
init|=
name|entries
operator|.
name|get
argument_list|(
name|DAY_OF_MONTH
argument_list|)
decl_stmt|;
name|CronEntry
name|month
init|=
name|entries
operator|.
name|get
argument_list|(
name|MONTH
argument_list|)
decl_stmt|;
name|CronEntry
name|dayOfWeek
init|=
name|entries
operator|.
name|get
argument_list|(
name|DAY_OF_WEEK
argument_list|)
decl_stmt|;
comment|// Start at the top of the next minute, cron is only guaranteed to be
comment|// run on the minute.
name|int
name|timeToNextMinute
init|=
literal|60
operator|-
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
decl_stmt|;
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
name|timeToNextMinute
argument_list|)
expr_stmt|;
comment|// If its already to late in the day this will roll us over to tomorrow
comment|// so we'll need to check again when done updating month and day.
name|int
name|currentMinutes
init|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|minutes
argument_list|,
name|currentMinutes
argument_list|)
condition|)
block|{
name|int
name|nextMinutes
init|=
name|getNext
argument_list|(
name|minutes
argument_list|,
name|currentMinutes
argument_list|)
decl_stmt|;
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
name|nextMinutes
argument_list|)
expr_stmt|;
block|}
name|int
name|currentHours
init|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|hours
argument_list|,
name|currentHours
argument_list|)
condition|)
block|{
name|int
name|nextHour
init|=
name|getNext
argument_list|(
name|hours
argument_list|,
name|currentHours
argument_list|)
decl_stmt|;
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
name|nextHour
argument_list|)
expr_stmt|;
block|}
comment|// We can roll into the next month here which might violate the cron setting
comment|// rules so we check once then recheck again after applying the month settings.
name|doUpdateCurrentDay
argument_list|(
name|working
argument_list|,
name|dayOfMonth
argument_list|,
name|dayOfWeek
argument_list|)
expr_stmt|;
comment|// Start by checking if we are in the right month, if not then calculations
comment|// need to start from the beginning of the month to ensure that we don't end
comment|// up on the wrong day.  (Can happen when DAY_OF_WEEK is set and current time
comment|// is ahead of the day of the week to execute on).
name|doUpdateCurrentMonth
argument_list|(
name|working
argument_list|,
name|month
argument_list|)
expr_stmt|;
comment|// Now Check day of week and day of month together since they can be specified
comment|// together in one entry, if both "day of month" and "day of week" are restricted
comment|// (not "*"), then either the "day of month" field (3) or the "day of week" field
comment|// (5) must match the current day or the Calenday must be advanced.
name|doUpdateCurrentDay
argument_list|(
name|working
argument_list|,
name|dayOfMonth
argument_list|,
name|dayOfWeek
argument_list|)
expr_stmt|;
comment|// Now we can chose the correct hour and minute of the day in question.
name|currentHours
operator|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|hours
argument_list|,
name|currentHours
argument_list|)
condition|)
block|{
name|int
name|nextHour
init|=
name|getNext
argument_list|(
name|hours
argument_list|,
name|currentHours
argument_list|)
decl_stmt|;
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
name|nextHour
argument_list|)
expr_stmt|;
block|}
name|currentMinutes
operator|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|minutes
argument_list|,
name|currentMinutes
argument_list|)
condition|)
block|{
name|int
name|nextMinutes
init|=
name|getNext
argument_list|(
name|minutes
argument_list|,
name|currentMinutes
argument_list|)
decl_stmt|;
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
name|nextMinutes
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|working
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|<=
name|currentTime
condition|)
block|{
throw|throw
operator|new
name|ArithmeticException
argument_list|(
literal|"Unable to compute next scheduled exection time."
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
specifier|static
name|long
name|doUpdateCurrentMonth
parameter_list|(
name|Calendar
name|working
parameter_list|,
name|CronEntry
name|month
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|int
name|currentMonth
init|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|month
argument_list|,
name|currentMonth
argument_list|)
condition|)
block|{
name|int
name|nextMonth
init|=
name|getNext
argument_list|(
name|month
argument_list|,
name|currentMonth
argument_list|)
decl_stmt|;
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|,
name|nextMonth
argument_list|)
expr_stmt|;
comment|// Reset to start of month.
name|resetToStartOfDay
argument_list|(
name|working
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|working
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
return|return
literal|0L
return|;
block|}
specifier|protected
specifier|static
name|long
name|doUpdateCurrentDay
parameter_list|(
name|Calendar
name|working
parameter_list|,
name|CronEntry
name|dayOfMonth
parameter_list|,
name|CronEntry
name|dayOfWeek
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|int
name|currentDayOfWeek
init|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_WEEK
argument_list|)
operator|-
literal|1
decl_stmt|;
name|int
name|currentDayOfMonth
init|=
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
decl_stmt|;
comment|// Simplest case, both are unrestricted or both match today otherwise
comment|// result must be the closer of the two if both are set, or the next
comment|// match to the one that is.
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|dayOfWeek
argument_list|,
name|currentDayOfWeek
argument_list|)
operator|||
operator|!
name|isCurrent
argument_list|(
name|dayOfMonth
argument_list|,
name|currentDayOfMonth
argument_list|)
condition|)
block|{
name|int
name|nextWeekDay
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|nextCalendarDay
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|dayOfWeek
argument_list|,
name|currentDayOfWeek
argument_list|)
condition|)
block|{
name|nextWeekDay
operator|=
name|getNext
argument_list|(
name|dayOfWeek
argument_list|,
name|currentDayOfWeek
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isCurrent
argument_list|(
name|dayOfMonth
argument_list|,
name|currentDayOfMonth
argument_list|)
condition|)
block|{
name|nextCalendarDay
operator|=
name|getNext
argument_list|(
name|dayOfMonth
argument_list|,
name|currentDayOfMonth
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nextWeekDay
operator|<
name|nextCalendarDay
condition|)
block|{
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|DAY_OF_WEEK
argument_list|,
name|nextWeekDay
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|working
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
name|nextCalendarDay
argument_list|)
expr_stmt|;
block|}
comment|// Since the day changed, we restart the clock at the start of the day
comment|// so that the next time will either be at 12am + value of hours and
comment|// minutes pattern.
name|resetToStartOfDay
argument_list|(
name|working
argument_list|,
name|working
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|working
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
return|return
literal|0L
return|;
block|}
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
specifier|final
name|String
name|cronEntry
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|tokenize
argument_list|(
name|cronEntry
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CronEntry
argument_list|>
name|entries
init|=
name|buildCronEntries
argument_list|(
name|list
argument_list|)
decl_stmt|;
for|for
control|(
name|CronEntry
name|e
range|:
name|entries
control|)
block|{
name|validate
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|validate
parameter_list|(
specifier|final
name|CronEntry
name|entry
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|entry
operator|.
name|currentWhen
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
operator|||
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
operator|<
name|entry
operator|.
name|start
operator|||
name|list
operator|.
name|get
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
operator|>
name|entry
operator|.
name|end
condition|)
block|{
throw|throw
operator|new
name|MessageFormatException
argument_list|(
literal|"Invalid token: "
operator|+
name|entry
argument_list|)
throw|;
block|}
block|}
specifier|static
name|int
name|getNext
parameter_list|(
specifier|final
name|CronEntry
name|entry
parameter_list|,
specifier|final
name|int
name|current
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|currentWhen
operator|==
literal|null
condition|)
block|{
name|entry
operator|.
name|currentWhen
operator|=
name|calculateValues
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|entry
operator|.
name|currentWhen
decl_stmt|;
name|int
name|next
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
if|if
condition|(
name|i
operator|.
name|intValue
argument_list|()
operator|>
name|current
condition|)
block|{
name|next
operator|=
name|i
operator|.
name|intValue
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|next
operator|!=
operator|-
literal|1
condition|)
block|{
name|result
operator|=
name|next
operator|-
name|current
expr_stmt|;
block|}
else|else
block|{
name|int
name|first
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|result
operator|=
name|entry
operator|.
name|end
operator|+
name|first
operator|-
name|entry
operator|.
name|start
operator|-
name|current
expr_stmt|;
comment|// Account for difference of one vs zero based indices.
if|if
condition|(
name|entry
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"DayOfWeek"
argument_list|)
operator|||
name|entry
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"Month"
argument_list|)
condition|)
block|{
name|result
operator|++
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|static
name|boolean
name|isCurrent
parameter_list|(
specifier|final
name|CronEntry
name|entry
parameter_list|,
specifier|final
name|int
name|current
parameter_list|)
throws|throws
name|MessageFormatException
block|{
name|boolean
name|result
init|=
name|entry
operator|.
name|currentWhen
operator|.
name|contains
argument_list|(
operator|new
name|Integer
argument_list|(
name|current
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
specifier|static
name|void
name|resetToStartOfDay
parameter_list|(
name|Calendar
name|target
parameter_list|,
name|int
name|day
parameter_list|)
block|{
name|target
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
name|day
argument_list|)
expr_stmt|;
name|target
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|target
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|target
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|tokenize
parameter_list|(
name|String
name|cron
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|StringTokenizer
name|tokenize
init|=
operator|new
name|StringTokenizer
argument_list|(
name|cron
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|tokenize
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|tokenize
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|!=
name|NUMBER_TOKENS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a valid cron entry - wrong number of tokens("
operator|+
name|result
operator|.
name|size
argument_list|()
operator|+
literal|"): "
operator|+
name|cron
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|calculateValues
parameter_list|(
specifier|final
name|CronEntry
name|entry
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAll
argument_list|(
name|entry
operator|.
name|token
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|entry
operator|.
name|start
init|;
name|i
operator|<=
name|entry
operator|.
name|end
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isAStep
argument_list|(
name|entry
operator|.
name|token
argument_list|)
condition|)
block|{
name|int
name|denominator
init|=
name|getDenominator
argument_list|(
name|entry
operator|.
name|token
argument_list|)
decl_stmt|;
name|String
name|numerator
init|=
name|getNumerator
argument_list|(
name|entry
operator|.
name|token
argument_list|)
decl_stmt|;
name|CronEntry
name|ce
init|=
operator|new
name|CronEntry
argument_list|(
name|entry
operator|.
name|name
argument_list|,
name|numerator
argument_list|,
name|entry
operator|.
name|start
argument_list|,
name|entry
operator|.
name|end
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|calculateValues
argument_list|(
name|ce
argument_list|)
decl_stmt|;
for|for
control|(
name|Integer
name|i
range|:
name|list
control|)
block|{
if|if
condition|(
name|i
operator|.
name|intValue
argument_list|()
operator|%
name|denominator
operator|==
literal|0
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|isAList
argument_list|(
name|entry
operator|.
name|token
argument_list|)
condition|)
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|entry
operator|.
name|token
argument_list|,
literal|","
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|str
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|CronEntry
name|ce
init|=
operator|new
name|CronEntry
argument_list|(
name|entry
operator|.
name|name
argument_list|,
name|str
argument_list|,
name|entry
operator|.
name|start
argument_list|,
name|entry
operator|.
name|end
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|calculateValues
argument_list|(
name|ce
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isARange
argument_list|(
name|entry
operator|.
name|token
argument_list|)
condition|)
block|{
name|int
name|index
init|=
name|entry
operator|.
name|token
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
name|int
name|first
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|entry
operator|.
name|token
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|last
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|entry
operator|.
name|token
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|first
init|;
name|i
operator|<=
name|last
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|value
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|entry
operator|.
name|token
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|isARange
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|token
operator|!=
literal|null
operator|&&
name|token
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|>=
literal|0
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|isAStep
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|token
operator|!=
literal|null
operator|&&
name|token
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|isAList
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|token
operator|!=
literal|null
operator|&&
name|token
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
operator|>=
literal|0
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|isAll
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|token
operator|!=
literal|null
operator|&&
name|token
operator|.
name|length
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|token
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'*'
operator|||
name|token
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'?'
operator|)
return|;
block|}
specifier|protected
specifier|static
name|int
name|getDenominator
parameter_list|(
specifier|final
name|String
name|token
parameter_list|)
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
name|int
name|index
init|=
name|token
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|token
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
name|result
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
specifier|static
name|String
name|getNumerator
parameter_list|(
specifier|final
name|String
name|token
parameter_list|)
block|{
name|int
name|index
init|=
name|token
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|token
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
return|return
name|str
return|;
block|}
specifier|static
name|List
argument_list|<
name|CronEntry
argument_list|>
name|buildCronEntries
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
block|{
name|List
argument_list|<
name|CronEntry
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|CronEntry
argument_list|>
argument_list|()
decl_stmt|;
name|CronEntry
name|minutes
init|=
operator|new
name|CronEntry
argument_list|(
literal|"Minutes"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
name|MINUTES
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|60
argument_list|)
decl_stmt|;
name|minutes
operator|.
name|currentWhen
operator|=
name|calculateValues
argument_list|(
name|minutes
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|minutes
argument_list|)
expr_stmt|;
name|CronEntry
name|hours
init|=
operator|new
name|CronEntry
argument_list|(
literal|"Hours"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
name|HOURS
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|24
argument_list|)
decl_stmt|;
name|hours
operator|.
name|currentWhen
operator|=
name|calculateValues
argument_list|(
name|hours
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|hours
argument_list|)
expr_stmt|;
name|CronEntry
name|dayOfMonth
init|=
operator|new
name|CronEntry
argument_list|(
literal|"DayOfMonth"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
name|DAY_OF_MONTH
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|)
decl_stmt|;
name|dayOfMonth
operator|.
name|currentWhen
operator|=
name|calculateValues
argument_list|(
name|dayOfMonth
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|dayOfMonth
argument_list|)
expr_stmt|;
name|CronEntry
name|month
init|=
operator|new
name|CronEntry
argument_list|(
literal|"Month"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
name|MONTH
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|month
operator|.
name|currentWhen
operator|=
name|calculateValues
argument_list|(
name|month
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|month
argument_list|)
expr_stmt|;
name|CronEntry
name|dayOfWeek
init|=
operator|new
name|CronEntry
argument_list|(
literal|"DayOfWeek"
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
name|DAY_OF_WEEK
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|dayOfWeek
operator|.
name|currentWhen
operator|=
name|calculateValues
argument_list|(
name|dayOfWeek
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|dayOfWeek
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|static
class|class
name|CronEntry
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|String
name|token
decl_stmt|;
specifier|final
name|int
name|start
decl_stmt|;
specifier|final
name|int
name|end
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|currentWhen
decl_stmt|;
name|CronEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|token
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
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
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
operator|+
literal|":"
operator|+
name|token
return|;
block|}
block|}
block|}
end_class

end_unit

