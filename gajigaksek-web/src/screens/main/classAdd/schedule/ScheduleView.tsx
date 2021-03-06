import React from "react";
import ClassAddNaviButton from "../ClassAddNaviButton";
import ScheduleBlock from "./ScheduleBlock";
import SchedulePopup, { ScheduleInfo } from "./popup/SchedulePopup";
import "./ScheduleView.css";
import { koDayList } from "../../../../utils/commonParams";
import LectureController from "../../../../services/controllers/LectureController";

interface ScheduleViewStates {
  schedules: ScheduleInfo[];
  popup: boolean;
  progressHour: number;
  progressMinute: number;
  minPeople: number;
  maxPeople: number;
}

const hour = [
  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
  22, 23, 24,
];

const minute = [0, 30];

export default class ScheduleView extends React.Component<
  Record<string, never>,
  ScheduleViewStates
> {
  constructor(props: any) {
    super(props);
    this.state = {
      schedules: [],
      popup: false,
      progressHour: 2,
      progressMinute: 0,
      minPeople: 1,
      maxPeople: 1,
    };
    this.getSchedule();
  }

  private async putSchedule(callback: () => void) {
    const params = {
      createLectureStep: "SCHEDULE",
      minParticipants: +this.state.minPeople,
      maxParticipants: +this.state.maxPeople,
      scheduleList: this.state.schedules,
    };

    if (!this.checkForm()) {
      return false;
    }

    const json = JSON.stringify(params);
    const blob = new Blob([json], {
      type: "application/json",
    });

    const formData = new FormData();
    formData.append("request", blob, "test1.json");
    formData.append("files", blob);

    try {
      await LectureController.putSchedule(formData);
      callback();
    } catch (e) {}
  }

  private async getSchedule() {
    try {
      const res = await LectureController.getSchedule();
      if (!res) return;

      this.setState({
        schedules: res.data.scheduleList,
        maxPeople: res.data.maxParticipants,
        minPeople: res.data.minParticipants,
      });
    } catch (e) {}
  }

  private checkForm() {
    if (this.state.progressHour < 1) {
      alert("????????? ????????? ?????? 1???????????? ??????????????????.");
      return false;
    }
    return true;
  }

  render() {
    return (
      <div className="class-add-first-container">
        <div className="class-add-first-header">
          <button
            className="class-add-first-back"
            onClick={() => (window.location.href = "/main/add/class/intro")}
          >
            <i className="fas fa-chevron-left fa-3x"></i>
          </button>
          <div className="class-add-first-header-title pretendard">
            ????????? ??????
          </div>
        </div>
        <div className="class-add-first-navigation">
          <ClassAddNaviButton on={false} title={"????????????"} />
          <ClassAddNaviButton on={false} title={"?????? ??????"} />
          <ClassAddNaviButton on={false} title={"????????????"} />
          <ClassAddNaviButton on title={"?????????"} />
          <ClassAddNaviButton on={false} title={"?????? ??? ??????"} />
          <ClassAddNaviButton on={false} title={"????????????"} />
        </div>
        <div className="class-add-first-main-container">
          <div className="class-add-first-name-container">
            <div className="class-add-schedule-header">
              <div className="class-add-mainCategory-title pretendard">
                ????????? ??????
              </div>
              <div className="class-add-schedule-time-wrapper">
                <div className="class-add-schedule-time-text1">????????????</div>
                <div className="class-add-schedule-time-select-container">
                  <select
                    onChange={(event: any) =>
                      this.setState({ progressHour: event.target.value })
                    }
                    value={this.state.progressHour}
                  >
                    {hour.map((h) => (
                      <option value={h}>{`${h}??????`}</option>
                    ))}
                  </select>
                  <select
                    onChange={(event: any) =>
                      this.setState({ progressMinute: event.target.value })
                    }
                    value={this.state.progressMinute}
                  >
                    {minute.map((m) => (
                      <option value={m}>{`${m}???`}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="class-add-schedule-time-wrapper">
                <div className="class-add-schedule-time-text1">??????</div>
                <div className="class-add-schedule-time-select-container">
                  <div className="class-add-schedule-popup-date-input-container add-flex-start">
                    <div className="class-add-schedule-popup-date-input-wrapper number-input">
                      <input
                        type="number"
                        className="pretendard"
                        min={1}
                        max={this.state.maxPeople}
                        value={this.state.minPeople}
                        onChange={(event: any) =>
                          this.setState({ minPeople: event.target.value })
                        }
                      ></input>
                      <div>???</div>
                    </div>
                    <div className="margin-vertical">~</div>
                    <div className="class-add-schedule-popup-date-input-wrapper number-input">
                      <input
                        type="number"
                        className="pretendard"
                        min={this.state.minPeople}
                        value={this.state.maxPeople}
                        onChange={(event: any) =>
                          this.setState({ maxPeople: event.target.value })
                        }
                      ></input>
                      <div>???</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div className="class-add-schedule-item-container">
              {this.state.schedules.map((item: ScheduleInfo) => {
                const date = new Date(item.lectureDate);

                const startDateFormat = `${date.getFullYear()}.${
                  date.getMonth() + 1
                }.${date.getDate()}(${koDayList[date.getDay()]}) ${
                  item.startHour < 10 ? "0" : ""
                }${item.startHour}??? ${item.startMinute < 10 ? "0" : ""}${
                  item.startMinute
                }???`;

                const endTimeMinute =
                  +(+item.startHour * 60) +
                  +item.startMinute +
                  +(+this.state.progressHour * 60) +
                  +this.state.progressMinute;

                const endHour = Math.floor(endTimeMinute / 60);

                const endMinute = endTimeMinute % 60;

                const endDateFormat = `${date.getFullYear()}.${
                  date.getMonth() + 1
                }.${date.getDate()}(${koDayList[date.getDay()]}) ${
                  endHour < 10 ? "0" : ""
                }${endHour}??? ${endMinute < 10 ? "0" : ""}${endMinute}???`;

                return (
                  <ScheduleBlock
                    date={`${startDateFormat} ~ ${endDateFormat}`}
                    people={`${this.state.minPeople} ~ ${this.state.maxPeople}???`}
                    onClick={() => {
                      this.setState({
                        schedules: this.state.schedules.filter(
                          (schedule) => schedule !== item
                        ),
                      });
                    }}
                  />
                );
              })}
            </div>
            <button
              className="class-add-schedule-button pretendard"
              onClick={() => this.setState({ popup: true })}
            >
              <i className="fas fa-plus fa-lg"></i>
              <div>????????? ??????</div>
            </button>
          </div>
        </div>
        <div className="class-add-first-bottom">
          <div>
            <button
              className="class-add-intro-bottom-left pretendard"
              onClick={() =>
                (window.location.href = "/main/add/class/curriculum")
              }
            >
              ??????
            </button>
            <button
              className="class-add-first-bottom-left pretendard"
              onClick={() => {
                this.putSchedule(
                  () => (window.location.href = "/main/add/class/price-coupon")
                );
              }}
            >
              ??????
            </button>
          </div>
          <button
            className="class-add-first-bottom-right pretendard"
            onClick={() =>
              this.putSchedule(() => alert("????????? ?????????????????????."))
            }
          >
            ??????
          </button>
        </div>

        <SchedulePopup
          progressMinute={
            +this.state.progressMinute + +(this.state.progressHour * 60)
          }
          on={this.state.popup}
          onClickExit={() => {
            this.setState({ popup: false });
          }}
          addSchedule={(infoList: ScheduleInfo[]) => {
            this.setState({
              schedules: [...this.state.schedules, ...infoList],
            });
          }}
        />
      </div>
    );
  }
}
