import React from "react";
import LectureController from "../../../../services/controllers/LectureController";
import { filterNumber } from "../../../../utils/commonParams";
import ClassAddNaviButton from "../ClassAddNaviButton";
import PriceBlock from "./PriceBlock";
import "./PriceCouponView.css";

interface PriceCouponViewStates {
  regularPrice: number;
  priceOne: number;
  priceTwo: number;
  priceThree: number;
  priceFour: number;
  couponPrice: number;
  couponCount: number;
}

export default class PriceCouponView extends React.Component<
  Record<string, never>,
  PriceCouponViewStates
> {
  constructor(props: any) {
    super(props);
    this.state = {
      regularPrice: 0,
      priceOne: 0,
      priceTwo: 0,
      priceThree: 0,
      priceFour: 0,
      couponPrice: 0,
      couponCount: 0,
    };

    this.getPriceCoupon();
  }

  private async putPriceCoupon(callback: () => void) {
    const params = {
      createLectureStep: "PRICE_COUPON",
      price: {
        regularPrice: this.state.regularPrice,
        priceOne: this.state.priceOne,
        priceTwo: this.state.priceTwo,
        priceThree: this.state.priceThree,
        priceFour: this.state.priceFour,
      },
      coupon: {
        couponPrice: this.state.couponPrice,
        couponCount: this.state.couponCount,
      },
    };

    const json = JSON.stringify(params);
    const blob = new Blob([json], {
      type: "application/json",
    });

    const formData = new FormData();
    formData.append("request", blob, "test1.json");
    formData.append("files", blob);

    try {
      await LectureController.putPriceCoupon(formData);
      callback();
    } catch (e) {}
  }

  private async getPriceCoupon() {
    try {
      const res = await LectureController.getPriceCoupon();
      const data = res?.data;
      this.setState({
        regularPrice: data.price.regularPrice,
        priceOne: data.price.priceOne,
        priceTwo: data.price.priceTwo,
        priceThree: data.price.priceThree,
        priceFour: data.price.priceFour,
        couponPrice: data.coupon.couponPrice,
        couponCount: data.coupon.couponCount,
      });
    } catch (e) {}
  }

  render() {
    return (
      <div className="class-add-first-container">
        <div className="class-add-first-header">
          <button
            className="class-add-first-back"
            onClick={() => (window.location.href = "/main/home")}
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
          <ClassAddNaviButton on={false} title={"?????????"} />
          <ClassAddNaviButton on title={"?????? ??? ??????"} />
          <ClassAddNaviButton on={false} title={"????????????"} />
        </div>
        <div className="class-add-first-main-container">
          <div className="class-add-first-name-container">
            <div className="class-add-mainCategory-title pretendard">??????</div>
            <div className="class-add-price-coupon-price-list">
              <PriceBlock
                price={this.state.regularPrice}
                title={"????????? ??????"}
                subTitle={""}
                onChangeText={(text: string) =>
                  this.setState({
                    regularPrice: filterNumber(text),
                  })
                }
              />
              <PriceBlock
                price={this.state.priceOne}
                title={"????????? ?????? ??????"}
                subTitle={"1???"}
                onChangeText={(text: string) =>
                  this.setState({
                    priceOne: filterNumber(text),
                  })
                }
              />
              <PriceBlock
                price={this.state.priceTwo}
                title={"????????? ?????? ??????"}
                subTitle={"2???"}
                onChangeText={(text: string) =>
                  this.setState({
                    priceTwo: filterNumber(text),
                  })
                }
              />
              <PriceBlock
                price={this.state.priceThree}
                title={"????????? ?????? ??????"}
                subTitle={"3???"}
                onChangeText={(text: string) =>
                  this.setState({
                    priceThree: filterNumber(text),
                  })
                }
              />
              <PriceBlock
                price={this.state.priceFour}
                title={"????????? ?????? ??????"}
                subTitle={"4??? ??????"}
                onChangeText={(text: string) =>
                  this.setState({
                    priceFour: filterNumber(text),
                  })
                }
              />
            </div>
          </div>

          <div className="class-add-first-name-container">
            <div className="class-add-mainCategory-title pretendard">
              ????????? ?????? ?????? ??????
            </div>
            <div className="class-add-price-coupon-coupon-wrapper">
              <div className="class-add-price-coupon-coupon-title pretendard">
                ?????? ?????? ??????
              </div>
              <div className="class-add-price-coupon-coupon-price-wrapper">
                <input
                  className="wtf-coupon pretendard"
                  type="text"
                  value={this.state.couponPrice}
                  onChange={(e: any) =>
                    this.setState({
                      couponPrice: filterNumber(e.target.value),
                    })
                  }
                ></input>
                <div className="pretendard">???</div>
              </div>
            </div>

            <div className="class-add-price-coupon-coupon-wrapper">
              <div className="class-add-price-coupon-coupon-title pretendard">
                ?????? ?????? ??????
              </div>
              <div className="class-add-price-coupon-coupon-input-wrapper">
                <button
                  onClick={() => {
                    if (this.state.couponCount > 0)
                      this.setState({
                        couponCount: this.state.couponCount - 1,
                      });
                  }}
                >
                  <i className="fas fa-minus fa-lg"></i>
                </button>
                <input
                  className="wtf-coupon pretendard"
                  value={this.state.couponCount}
                ></input>
                ???
                <button
                  onClick={() =>
                    this.setState({ couponCount: this.state.couponCount + 1 })
                  }
                >
                  <i className="fas fa-plus fa-lg"></i>
                </button>
              </div>
              <div className="fucking-info pretendard">
                ????????? ???????????? ?????? 30?????? ?????? ???????????????.
              </div>
            </div>
          </div>
        </div>
        <div className="class-add-first-bottom">
          <div>
            <button
              className="class-add-intro-bottom-left pretendard"
              onClick={() =>
                (window.location.href = "/main/add/class/schedule")
              }
            >
              ??????
            </button>
            <button
              className="class-add-first-bottom-left pretendard"
              onClick={() =>
                this.putPriceCoupon(
                  () => (window.location.href = "/main/add/class/terms")
                )
              }
            >
              ??????
            </button>
          </div>
          <button
            className="class-add-first-bottom-right pretendard"
            onClick={() =>
              this.putPriceCoupon(() => alert("????????? ?????????????????????."))
            }
          >
            ??????
          </button>
        </div>
      </div>
    );
  }
}
